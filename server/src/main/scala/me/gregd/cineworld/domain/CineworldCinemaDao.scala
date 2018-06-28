package me.gregd.cineworld.domain

import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Film, Performance}
import me.gregd.cineworld.domain.movies.MovieDao
import me.gregd.cineworld.integration.PostcodeService
import me.gregd.cineworld.integration.cineworld.CineworldService
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CineworldCinemaDao(
                          dao: CineworldService,
                          postcodeService: PostcodeService
) extends CinemaDao
    with LazyLogging {

  implicit val formats = DefaultFormats

  override def retrieveCinemas(): Future[Seq[Cinema]] =
    for {
      rawCinemas <- dao.retrieveCinemas()
      postcodes = rawCinemas.map(_.postcode)
      coordinates <- postcodeService.lookup(postcodes)
    } yield {
      rawCinemas.map( raw =>
        CineworldTransformer.toCinema(raw, coordinates.get(raw.postcode))
      )
    }

  override def retrieveMoviesAndPerformances(cinemaId: String, date: LocalDate): Future[Map[Film, Seq[Performance]]] = {

    dao.retrieveListings(cinemaId, date).map { listingsBody =>
      logger.debug(s"Retrieving listings for $cinemaId:$date")
      val performancesById = listingsBody.events.groupBy(_.filmId)

      val res = for {
        raw <- listingsBody.films
        film = CineworldTransformer.toFilm(raw)
        performances = performancesById(raw.id).map(CineworldTransformer.toPerformances)
      } yield film -> performances

      res.toMap
    }
  }
}
