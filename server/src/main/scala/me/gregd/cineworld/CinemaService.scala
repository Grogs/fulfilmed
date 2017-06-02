package me.gregd.cineworld

import javax.inject.Inject

import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.cinema.vue.VueCinemaDao
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, CinemaApi, Movie, Performance}
import me.gregd.cineworld.util.Clock

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CinemaService @Inject()(movieDao: MovieDao, cineworld: CineworldCinemaDao, vue: VueCinemaDao, clock: Clock) extends CinemaApi {

  override def getMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] =
    cineworld.retrieveMoviesAndPerformances(cinemaId, parse(dateRaw))

  override def getCinemas(): Future[Seq[(Chain, Map[Grouping, Seq[Cinema]])]] = {
    def isLondon(s: Cinema) = if (s.id startsWith "London - ") "London cinemas" else "All other cinemas"
    cineworld.retrieveCinemas().map(_.groupBy(isLondon)).map(r => Seq("Cineworld" -> r))
  }

  private def parse(s: String) = s match {
    case "today" => clock.today().toString
    case "tomorrow" => (clock.today() plusDays 1).toString
    case other => other
  }
}

