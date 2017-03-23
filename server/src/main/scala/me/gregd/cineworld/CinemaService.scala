package me.gregd.cineworld

import java.time.LocalDate.now
import javax.inject.Inject

import me.gregd.cineworld.dao.cineworld.CinemaDao
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CinemaService @Inject()(movieDao: MovieDao, cinemaDao: CinemaDao) extends ServerCinemaApi {

  override def getMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] =
    cinemaDao.retrieveMoviesAndPerformances(cinemaId, parse(dateRaw))

  override def getCinemas(): Future[Seq[(Chain, Map[Grouping, Seq[Cinema]])]] =
    cinemaDao.retrieveCinemas().map(
        _.groupBy(s => if (s.id startsWith "London - ") "London cinemas" else "All other cinemas")
    ).map(r => Seq("Cineworld" -> r))

  private def parse(s: String) = s match {
    case "today" => now().toString
    case "tomorrow" => (now() plusDays 1).toString
    case other => other
  }
}

