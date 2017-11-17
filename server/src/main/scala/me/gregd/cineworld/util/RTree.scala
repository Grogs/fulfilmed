package me.gregd.cineworld.util

import com.github.davidmoten.rtree.geometry.{Geometries, Point}
import com.github.davidmoten.rtree.internal.EntryDefault
import com.github.davidmoten.rtree.{RTree => UnderlyingRTree}
import me.gregd.cineworld.domain.Coordinates
import rx.Observable

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

class RTree[T](items: Seq[(Coordinates, T)]) {

  private val tree: UnderlyingRTree[T, Point] = {
    val entries = for {
      (coordinates, item) <- items
    } yield EntryDefault.entry(item, toPoint(coordinates))
    UnderlyingRTree.star().create(new java.util.ArrayList(entries.asJava))
  }

  def nearest(coordinates: Coordinates, maxDistance: Double, maxCount: Int): Future[List[T]] = {
    val point = toPoint(coordinates)
    val searchResults = tree.nearest(point, maxDistance, maxCount).toList
    for {
      results <- toFuture(searchResults)
    } yield results.asScala.map(_.value()).toList
  }

  private def toPoint(coordinates: Coordinates) = Geometries.pointGeographic(coordinates.lat, coordinates.long)
  private def toFuture[T](obs: Observable[T]): Future[T] = {
    val res = Promise[T]()
    obs.subscribe(
      result => res.success(result),
      error => res.failure(error)
    )
    res.future
  }

}
