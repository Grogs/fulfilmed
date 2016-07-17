package me.gregd.cineworld

import java.time.LocalDate
import javax.inject.Inject

import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cineworld.CineworldDao
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, CinemaApi, Movie, Performance}
import play.api.Environment

import scala.concurrent.{ExecutionContext, Future}

class CinemaService @Inject()(env: Environment, movieDao: MovieDao, cineworldDao: CineworldDao, implicit val movies: MovieDao, implicit val tmdb: TheMovieDB) extends CinemaApi {

  implicit val ec = ExecutionContext.global

  def getDate(s: String): LocalDate = s match {
    case "today" => LocalDate.now()
    case "tomorrow" => LocalDate.now() plusDays 1
    case other => LocalDate.parse(s)
  }

  def getMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] =
    cineworldDao.retrieve7DayListings(cinemaId).map(
      _.flatMap(
        CineworldDao.toMovie(cinemaId, _)
          .map { case (k, v) =>
            movieDao.toMovie(k) -> v.getOrElse(getDate(dateRaw), Nil).toList
          }
          .filter(_._2.nonEmpty)
      ).toMap
    )

  override def getCinemas(): Future[Seq[(Chain, Map[Grouping, Seq[Cinema]])]] =
    cineworldDao.retrieveCinemas().map(
      _.map(CineworldDao.toCinema)
        .groupBy(s => if (s.id startsWith "London - ") "London cinemas" else "All other cinemas")
    ).map(r => Seq("Cineworld" -> r))
}
