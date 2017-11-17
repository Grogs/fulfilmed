package me.gregd.cineworld

import javax.inject.Inject

import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.cinema.vue.VueCinemaDao
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain._
import me.gregd.cineworld.util.{Clock, RTree}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.math._

class CinemaService @Inject()(movieDao: MovieDao, cineworld: CineworldCinemaDao, vue: VueCinemaDao, clock: Clock) extends CinemaApi {

  private lazy val tree: Future[RTree[Cinema]] =
    getCinemasFlat().map { cinemas =>
      val coordsAndCinemas = for {
        cinema <- cinemas
        coordinates <- cinema.coordinates
      } yield coordinates -> cinema
      new RTree(coordsAndCinemas)
    }

  override def getNearbyCinemas(coordinates: Coordinates): Future[Seq[Cinema]] = {
    def distance(c: Cinema): Double = c.coordinates.map(c => haversine(coordinates, c)).getOrElse(Double.MaxValue)
    val maxDistance = 50
    val maxResults = 10
    val nearbyCinemas = tree.flatMap(_.nearest(coordinates, maxDistance, maxResults))
    nearbyCinemas.map { cinemas =>
      val modifyAndKeepDistance = for {
        cinema <- cinemas
        name = cinema.name
        chain = cinema.chain
        distKm = distance(cinema)
        dist = "%.1f".format(distKm)
      } yield distKm -> cinema.copy(name = s"$chain - $name ($dist km)")
      modifyAndKeepDistance.sortBy(_._1).map(_._2)
    }
  }

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
    val allCinemas = getCinemasFlat()
    allCinemas.map(all => all.groupBy(_.chain).mapValues(_.groupBy(isLondon)))
  }

  private def getCinemasFlat() =
    Future.sequence(List(cineworld.retrieveCinemas(), vue.retrieveCinemas())).map(_.flatten)

  private def parse(s: String) = s match {
    case "today"    => clock.today().toString
    case "tomorrow" => (clock.today() plusDays 1).toString
    case other      => other
  }

  def haversine(pos1: Coordinates, pos2: Coordinates) = {
    val R = 6372.8 //radius in km
    val dLat = (pos2.lat - pos1.lat).toRadians
    val dLon = (pos2.long - pos1.long).toRadians

    val a = pow(sin(dLat / 2), 2) + pow(sin(dLon / 2), 2) * cos(pos1.lat.toRadians) * cos(pos2.lat.toRadians)
    val c = 2 * asin(sqrt(a))
    R * c
  }

}
