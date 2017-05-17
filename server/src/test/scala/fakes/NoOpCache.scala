package fakes

import me.gregd.cineworld.Cache

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scalacache.ScalaCache
import scalacache.serialization.Codec

object NoOpCache extends scalacache.Cache[Array[Byte]] {
  val cache = Cache(ScalaCache(this))

  def get[V](key: String)(implicit codec: Codec[V, Array[Byte]]): Future[Option[V]] =
    Future.successful(None)

  def put[V](key: String, value: V, ttl: Option[Duration])(implicit codec: Codec[V, Array[Byte]]): Future[Unit] =
    Future.successful(())

  def remove(key: String): Future[Unit] =
    Future.successful(())

  def removeAll(): Future[Unit] =
    Future.successful(())

  def close(): Unit =
    ()
}
