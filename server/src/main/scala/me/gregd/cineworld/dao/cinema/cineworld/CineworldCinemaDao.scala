package me.gregd.cineworld.dao.cinema.cineworld

import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.PostcodeService
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.CinemaDao
import me.gregd.cineworld.dao.cinema.cineworld.raw.{CineworldRepository, CineworldRepositoryTransformer}
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain._
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CineworldCinemaDao(
    imdb: MovieDao,
    tmdb: TheMovieDB,
    dao: CineworldRepository,
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
        CineworldRepositoryTransformer.toCinema(raw, coordinates.get(raw.postcode))
      )
    }

  override def retrieveMoviesAndPerformances(cinemaId: String, date: LocalDate): Future[Map[Movie, Seq[Performance]]] = {

    dao.retrieveListings(cinemaId, date).flatMap { listingsBody =>
      logger.debug(s"Retrieving listings for $cinemaId:$date")
      val performancesById = listingsBody.events.groupBy(_.filmId)

      val res = for {
        raw <- listingsBody.films
        film = CineworldRepositoryTransformer.toFilm(raw)
        movie = imdb.toMovie(film)
        performances = performancesById(raw.id).map(CineworldRepositoryTransformer.toPerformances)
      } yield movie.map(_ -> performances)

      Future.sequence(res).map(_.toMap)
    }
  }
}
