package me.gregd.cineworld.dao.cinema.vue

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import me.gregd.cineworld.dao.cinema.CinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.cinema.vue.raw.model.listings.Showings
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain._
import me.gregd.cineworld.util.Clock

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VueCinemaDao(vueRepository: VueRepository, imdb: MovieDao, clock: Clock) extends CinemaDao {

  private val timeFormat = DateTimeFormatter ofPattern "h:m a"

  def retrieveCinemas(): Future[Seq[Cinema]] = {
    val res = vueRepository
      .retrieveCinemas()
      .map(raw =>
        for { c <- raw } yield {
          vueRepository
            .retrieveLocation(c)
            .map(loc => {
              val coordinatesOpt = loc.map { case (lat, long) => Coordinates(lat, long) }
              Cinema(c.id, "Vue", c.search_term, coordinatesOpt)
            })
      })

    res.map(Future.sequence(_)).flatten
  }

  def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] = {
    vueRepository.retrieveListings(cinemaId).flatMap { raw =>
      val date = LocalDate.parse(dateRaw)

      val converted = for {
        f <- raw.films
        image = ImageUrl.resolve(f.image_poster)
        film = Film(f.id, f.title, image)
        urlBuilder = (sessionId: String) => s"https://www.myvue.com/book-tickets/summary/$cinemaId/${film.id}/$sessionId"
        showings = f.showings
        performances = filterAndBuild(date, showings, urlBuilder)
        if performances.nonEmpty
        movie = imdb.toMovie(film)
      } yield movie.map(_ -> performances)

      Future.sequence(converted).map(_.toMap)
    }
  }

  private def filterAndBuild(date: LocalDate, showings: List[Showings], urlBuilder: String => String) = {

    def isStale(time: LocalTime) = date == clock.today() && (time.minusHours(1) isBefore clock.time())

    for {
      s <- showings
      if date == LocalDate.parse(s.date_time)
      t <- s.times
      time = LocalTime.parse(t.time, timeFormat)
      if !isStale(time)
    } yield Performance(t.time, available = true, t.screen_type, urlBuilder(t.session_id), Option(s.date_time))

  }
}
