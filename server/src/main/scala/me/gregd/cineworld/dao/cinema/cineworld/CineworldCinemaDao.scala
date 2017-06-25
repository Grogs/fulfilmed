package me.gregd.cineworld.dao.cinema.cineworld

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.CinemaDao
import me.gregd.cineworld.dao.cinema.cineworld.raw.{CineworldRepository, CineworldRepositoryTransformer}
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, Film, Movie, Performance}
import me.gregd.cineworld.util.Clock
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

@Singleton
class CineworldCinemaDao @Inject()(
    imdb: MovieDao,
    tmdb: TheMovieDB,
    dao: CineworldRepository
) extends CinemaDao
    with LazyLogging {

  implicit val formats = DefaultFormats

  override def retrieveCinemas(): Future[Seq[Cinema]] =
    dao
      .retrieveCinemas()
      .map(
        _.map(CineworldRepositoryTransformer.toCinema)
      )

  override def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] = {

    def sequence[K, V](m: Map[Future[K], V]): Future[Map[K, V]] = {
      import cats.Traverse, cats.instances.all._
      Traverse[({type M[A] = Map[V, A] })#M].sequence(m.map(_.swap)).map(_.map(_.swap))
    }

    val date = LocalDate.parse(dateRaw)

    dao.retrieve7DayListings(cinemaId).flatMap { rawMovies =>
      logger.debug(s"Retrieving listings for $cinemaId:$dateRaw")
      val res = for {
        movieResp <- rawMovies
        (film, allPerformances) <- CineworldRepositoryTransformer.toMovie(cinemaId, movieResp)
        movie = imdb.toMovie(film)
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
