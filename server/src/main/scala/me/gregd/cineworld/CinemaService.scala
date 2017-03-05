package me.gregd.cineworld

import javax.inject.Inject

import me.gregd.cineworld.dao.cineworld.CineworldDao
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, CinemaApi, Movie, Performance}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CinemaService @Inject()(movieDao: MovieDao, cineworldDao: CineworldDao) extends CinemaApi {

  override def getMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] =
    cinemaDao.retrieveMoviesAndPerformances(cinemaId, dateRaw)

  override def getCinemas(): Future[Seq[(Chain, Map[Grouping, Seq[Cinema]])]] =
    cinemaDao.retrieveCinemas().map(
        _.groupBy(s => if (s.id startsWith "London - ") "London cinemas" else "All other cinemas")
    ).map(r => Seq("Cineworld" -> r))
}
