package me.gregd.cineworld.util

import cats.effect.Async
import com.github.davidmoten.rtree.geometry.{Geometries, Point}
import com.github.davidmoten.rtree.internal.EntryDefault
import com.github.davidmoten.rtree.{RTree => UnderlyingRTree}
import me.gregd.cineworld.domain.model.Coordinates
import rx.Observable

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

  def nearest(coordinates: Coordinates, maxDistance: Double, maxCount: Int): F[List[T]] = {
    val point         = toPoint(coordinates)
    val searchResults = tree.nearest(point, maxDistance, maxCount).toList
    for {
      results <- toAsync(searchResults)
    } yield results.asScala.map(_.value()).toList
  }

  private def toPoint(coordinates: Coordinates) = Geometries.pointGeographic(coordinates.lat, coordinates.long)

  private def toAsync[T](obs: Observable[T]): F[T] = {
    Async[F].async[T](
      cb =>
        obs.subscribe(
          result => cb(Right(result)),
          error => cb(Left(error))
      ))
  }

}
