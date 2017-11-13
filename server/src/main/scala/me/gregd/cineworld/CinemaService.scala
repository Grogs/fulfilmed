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
    for {
      c <- cineworld.retrieveMoviesAndPerformances(cinemaId, parse(dateRaw))
      v <- vue.retrieveMoviesAndPerformances(cinemaId, parse(dateRaw))
    } yield c ++ v
  }

  override def getCinemas(): Future[Map[Chain, Map[Grouping, Seq[Cinema]]]] = {
    def isLondon(s: Cinema) = if (s.name startsWith "London - ") "London cinemas" else "All other cinemas"
    val allCinemas = Future.sequence(List(cineworld.retrieveCinemas(), vue.retrieveCinemas()))
    allCinemas.map(all =>
      all.flatten.groupBy(_.chain).mapValues(_.groupBy(isLondon))
    )
  }

  private def parse(s: String) = s match {
    case "today" => clock.today().toString
    case "tomorrow" => (clock.today() plusDays 1).toString
    case other => other
  }

  override def getNearbyCinemas(lat: Double, long: Double) = {
    def distance(c: Cinema): Double = c.coordinates.map(c => haversine(lat, long, c.lat, c.long)).getOrElse(Double.MaxValue)
    val allCinemas = Future.sequence(List(cineworld.retrieveCinemas(), vue.retrieveCinemas()))
    allCinemas.map(allCinemas =>
      for {
        cinema <- allCinemas.flatten.sortBy(distance).take(10)
        name = cinema.name
        chain = cinema.chain
        dist = "%.1f".format(distance(cinema))
      } yield cinema.copy(name = s"$chain - $name ($dist km)")
    )
  }

  val R = 6372.8  //radius in km

  def haversine(lat1:Double, lon1:Double, lat2:Double, lon2:Double)={
    val dLat=(lat2 - lat1).toRadians
    val dLon=(lon2 - lon1).toRadians

    val a = pow(sin(dLat/2),2) + pow(sin(dLon/2),2) * cos(lat1.toRadians) * cos(lat2.toRadians)
    val c = 2 * asin(sqrt(a))
    R * c
  }

}

