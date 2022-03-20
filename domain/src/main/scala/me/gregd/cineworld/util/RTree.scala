package me.gregd.cineworld.util

import cats.effect.Async
import com.github.davidmoten.rtree2.geometry.{Geometries, Point}
import com.github.davidmoten.rtree2.internal.EntryDefault
import com.github.davidmoten.rtree2.{RTree => UnderlyingRTree}
import me.gregd.cineworld.domain.model.Coordinates

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}
import cats.syntax.functor._

class RTree[T, F[_]: Async](items: Seq[(Coordinates, T)]) {

  private val tree: UnderlyingRTree[T, Point] = {
    val entries = for {
      (coordinates, item) <- items
    } yield EntryDefault.entry(item, toPoint(coordinates))
    UnderlyingRTree.star().create(new java.util.ArrayList(entries.asJava))
  }

  def nearest(coordinates: Coordinates, maxDistance: Double, maxCount: Int): List[T] = {
    val point         = toPoint(coordinates)
    val searchResults = tree.nearest(point, maxDistance, maxCount).asScala
    searchResults.map(_.value()).toList
  }

  private def toPoint(coordinates: Coordinates) = Geometries.pointGeographic(coordinates.lat, coordinates.long)
}
