package me.gregd.cineworld.domain.service

import cats.effect.IO

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}
import me.gregd.cineworld.domain.model.{Cinema, Coordinates, Film, Performance}
import me.gregd.cineworld.integration.vue.{ImageUrl, VueIntegrationService}
import me.gregd.cineworld.integration.vue.listings.Showings
import me.gregd.cineworld.util.Clock

import java.util.Locale
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VueService(underlying: VueIntegrationService, clock: Clock) {

  private val timeFormat = DateTimeFormatter.ofPattern("h:m a", Locale.UK)

  def retrieveCinemas(): IO[List[Cinema]] = {
    import cats.implicits._
    underlying
      .retrieveCinemas()
      .flatMap(
        _.traverse(
          vueCinema =>
            underlying
              .retrieveLocation(vueCinema)
              .map { loc =>
                val coordinatesOpt = loc.map { case (lat, long) => Coordinates(lat, long) }
                Cinema(vueCinema.id, "Vue", vueCinema.search_term, coordinatesOpt)
            })
      )
  }

  def retrieveMoviesAndPerformances(cinemaId: String, date: LocalDate): IO[Map[Film, List[Performance]]] = {
    underlying.retrieveListings(cinemaId).map { raw =>
      val converted = for {
        f <- raw.films
        image        = ImageUrl.resolve(f.image_poster)
        film         = Film(f.id, f.title, image)
        urlBuilder   = (sessionId: String) => s"https://www.myvue.com/book-tickets/summary/$cinemaId/${film.id}/$sessionId"
        showings     = f.showings
        performances = filterAndBuild(date, showings, urlBuilder)
        if performances.nonEmpty
      } yield film -> performances

      converted.toMap
    }
  }

  private def filterAndBuild(date: LocalDate, showings: List[Showings], urlBuilder: String => String) = {

    def isStale(time: LocalTime) = date == clock.today() && (time.minusHours(1) isBefore clock.time())

    for {
      s <- showings
      if date == LocalDate.parse(s.date_time)
      t <- s.times
      time = LocalTime.parse(t.time.toLowerCase, timeFormat)
      if !isStale(time)
    } yield Performance(t.time, available = true, t.screen_type, urlBuilder(t.session_id), Option(s.date_time))

  }
}
