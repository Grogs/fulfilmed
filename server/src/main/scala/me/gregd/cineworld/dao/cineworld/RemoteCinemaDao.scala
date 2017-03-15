package me.gregd.cineworld.dao.cineworld

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import cats.instances.future._
import cats.instances.tuple._
import cats.syntax.bitraverse._
import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Movie, Performance}
import org.json4s._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class RemoteCinemaDao @Inject()(
                                 imdb: MovieDao,
                                 tmdb: TheMovieDB,
                                 dao: CineworldRepository
                               ) extends CinemaDao with Logging {
  val decode = java.net.URLDecoder.decode(_: String, "UTF-8")
  implicit val formats = DefaultFormats
  implicit val ec = ExecutionContext.global

  private def getDate(s: String): LocalDate = s match {
    case "today" => LocalDate.now()
    case "tomorrow" => LocalDate.now() plusDays 1
    case other => LocalDate.parse(s)
  }


  override def retrieveCinemas() =
    dao.retrieveCinemas().map(
      _.map(CineworldRepository.toCinema)
    )

  override def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String) = {

    dao.retrieve7DayListings(cinemaId).map { rawMovies =>
      val res = for {
        movieResp <- rawMovies
        (film, allPerformances) <- CineworldRepository.toMovie(cinemaId, movieResp)
        movie = imdb.toMovie(film)
        performances = allPerformances.getOrElse(getDate(dateRaw), Nil).toList
        if performances.nonEmpty
        performancesF = performances
        res = movie -> performancesF
        _ = logger.debug(s"Retrieved listings for $cinemaId:$dateRaw:${film.id}")
      } yield res
      res.toMap
    }
  }


  def sequence[A, B](t: (Future[A], Future[B])): Future[(A, B)] = t match {
    case (a, b) =>
      for {
        a <- a
        b <- b
      } yield (a, b)
  }

}