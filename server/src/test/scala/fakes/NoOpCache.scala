package fakes

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scalacache.Cache
import scalacache.serialization.Codec

object NoOpCache extends Cache[Array[Byte]] {
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
