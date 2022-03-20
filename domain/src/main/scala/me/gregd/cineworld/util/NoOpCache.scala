package me.gregd.cineworld.util

import cats.effect.{IO, Sync}
import scalacache.logging.Logger

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scalacache.serialization.Codec

class NoOpCache[V] extends scalacache.AbstractCache[IO,String,V] {
  protected def F: Sync[IO] = implicitly

  protected def doGet(key: String): IO[Option[V]] = IO.pure(None)

  protected def doPut(key: String, value: V, ttl: Option[Duration]): IO[Unit] = IO.unit

  protected def doRemove(key: String): IO[Unit] = IO.unit

  protected def doRemoveAll: IO[Unit] = IO.unit

  def close: IO[Unit] = IO.unit

  override protected final val logger = Logger.getLogger(getClass.getName)
}
