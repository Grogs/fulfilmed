package me.gregd.cineworld.dao.cinema.vue

import java.time.LocalDate
import javax.inject.Inject

import me.gregd.cineworld.dao.cinema.CinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.cinema.vue.raw.model.listings.Showings
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, Film, Movie, Performance}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VueCinemaDao @Inject() (vueRepository: VueRepository, imdb: MovieDao) extends CinemaDao {

  def retrieveCinemas(): Future[Seq[Cinema]] = {
    vueRepository.retrieveCinemas().map( raw =>
      for { c <- raw } yield Cinema(c.id, c.name)
    )
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
    for {
      s <- showings
      if date == LocalDate.parse(s.date_time)
      t <- s.times
    } yield
      Performance(t.time, available = true, t.screen_type, urlBuilder(t.session_id), Option(s.date_time))

  }
}
