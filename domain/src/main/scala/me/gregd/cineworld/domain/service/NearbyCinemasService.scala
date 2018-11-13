package me.gregd.cineworld.domain.service

import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import me.gregd.cineworld.domain.repository.CinemaRepository
import me.gregd.cineworld.util.RTree

import scala.math._

class NearbyCinemasService[F[_]: Async](cinemaRepository: CinemaRepository[F]) extends NearbyCinemas[F] with LazyLogging {

  private lazy val tree: F[RTree[Cinema, F]] =
    cinemaRepository.fetchAll().map{ cinemas =>
      val coordsAndCinemas = for {
        cinema <- cinemas
        coordinates <- cinema.coordinates
      } yield coordinates -> cinema
      new RTree(coordsAndCinemas)
    }

  def getNearbyCinemas(coordinates: Coordinates): F[Seq[Cinema]] = {
    def distance(c: Cinema): Double = c.coordinates.map(c => haversine(coordinates, c)).getOrElse(Double.MaxValue)
    val maxDistance = 150
    val maxResults = 50
    val nearbyCinemas = tree.flatMap(_.nearest(coordinates, maxDistance, maxResults))
    nearbyCinemas.map { cinemas =>
      val modifyAndKeepDistance = for {
        cinema <- cinemas
        name = cinema.name
        chain = cinema.chain
        distKm = distance(cinema)
        dist = "%.1f".format(distKm)
      } yield distKm -> cinema.copy(name = s"$chain - $name ($dist km)")
      modifyAndKeepDistance.sortBy(_._1).map(_._2).take(10)
    }
  }

  private def haversine(pos1: Coordinates, pos2: Coordinates) = {
    val R = 6372.8 //radius in km
    val dLat = (pos2.lat - pos1.lat).toRadians
    val dLon = (pos2.long - pos1.long).toRadians

    val a = pow(sin(dLat / 2), 2) + pow(sin(dLon / 2), 2) * cos(pos1.lat.toRadians) * cos(pos2.lat.toRadians)
    val c = 2 * asin(sqrt(a))
    R * c
  }

}
