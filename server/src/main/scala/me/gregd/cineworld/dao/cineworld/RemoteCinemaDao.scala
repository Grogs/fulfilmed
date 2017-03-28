package me.gregd.cineworld.dao.cineworld

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, Film, Movie, Performance}
import me.gregd.cineworld.util.Clock
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

@Singleton
class RemoteCinemaDao @Inject()(
    imdb: MovieDao,
    tmdb: TheMovieDB,
    dao: CineworldRepository,
    clock: Clock
) extends CinemaDao
    with Logging {

  val decode = java.net.URLDecoder.decode(_: String, "UTF-8")
  implicit val formats = DefaultFormats

  private def getDate(s: String): Try[LocalDate] = {
    Try(LocalDate.parse(s))
      .filter { date =>
        val fromToday = date.toEpochDay - clock.today().toEpochDay
        fromToday >= 0 && fromToday <= 7
      }
  }

  override def retrieveCinemas(): Future[Seq[Cinema]] =
    dao
      .retrieveCinemas()
      .map(
        _.map(CineworldRepository.toCinema)
      )

  override def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] = {

    def sequence[K,V](m: Map[Future[K], V]): Future[Map[K,V]] = {
      import cats._,instances.all._,syntax._
      Traverse[({type M[A] = Map[V, A]})#M].sequence(m.map(_.swap)).map(_.map(_.swap))
    }

    dao.retrieve7DayListings(cinemaId).flatMap { rawMovies =>
      logger.info(s"Retrieving listings for $cinemaId:$dateRaw")
      val res = for {
        movieResp <- rawMovies
        (film, allPerformances) <- CineworldRepository.toMovie(cinemaId, movieResp)
        movie = imdb.toMovie(film)
        date = getDate(dateRaw).get
        performances = allPerformances.getOrElse(date, Nil).toList
        if performances.nonEmpty
      } yield movie -> performances
      sequence(res.toMap)
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
