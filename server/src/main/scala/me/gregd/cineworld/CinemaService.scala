package me.gregd.cineworld

import javax.inject.Inject

import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.cinema.vue.VueCinemaDao
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain._
import me.gregd.cineworld.util.Clock

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.math._

class CinemaService @Inject()(movieDao: MovieDao, cineworld: CineworldCinemaDao, vue: VueCinemaDao, clock: Clock) extends CinemaApi {

  override def getMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] = {
    //Relying on IDs not conflicting
    Future
      .sequence(
        Seq(
          cineworld.retrieveMoviesAndPerformances(cinemaId, parse(dateRaw)),
          vue.retrieveMoviesAndPerformances(cinemaId, parse(dateRaw))
        ))
      .map(_.flatten.toMap)
  }

  override def getCinemas(): Future[Map[Chain, Map[Grouping, Seq[Cinema]]]] = {
    def isLondon(s: Cinema) = if (s.name startsWith "London - ") "London cinemas" else "All other cinemas"
    val allCinemas = Future.sequence(List(cineworld.retrieveCinemas(), vue.retrieveCinemas()))
    allCinemas.map(all => all.flatten.groupBy(_.chain).mapValues(_.groupBy(isLondon)))
  }

  private def parse(s: String) = s match {
    case "today"    => clock.today().toString
    case "tomorrow" => (clock.today() plusDays 1).toString
    case other      => other
  }

  override def getNearbyCinemas(coordinates: Coordinates) = {
    def distance(c: Cinema): Double = c.coordinates.map(c => haversine(coordinates, c)).getOrElse(Double.MaxValue)
    val allCinemas = Future.sequence(List(cineworld.retrieveCinemas(), vue.retrieveCinemas()))
    allCinemas.map(allCinemas =>
      for {
        cinema <- allCinemas.flatten.sortBy(distance).take(10)
        name = cinema.name
        chain = cinema.chain
        dist = "%.1f".format(distance(cinema))
      } yield cinema.copy(name = s"$chain - $name ($dist km)"))
  }

  val R = 6372.8 //radius in km

  def haversine(pos1: Coordinates, pos2: Coordinates) = {
    val dLat = (pos2.lat - pos1.lat).toRadians
    val dLon = (pos2.long - pos1.long).toRadians

    val a = pow(sin(dLat / 2), 2) + pow(sin(dLon / 2), 2) * cos(pos1.lat.toRadians) * cos(pos2.lat.toRadians)
    val c = 2 * asin(sqrt(a))
    R * c
  }

}
