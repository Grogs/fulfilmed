package me.gregd.cineworld.util

import cats.effect.{IO, Sync}
import cats.effect.unsafe.Scheduler
import cats.implicits.catsSyntaxApplicativeError
import fs2.{Chunk, Collector, Stream}
import fs2.io.file.{Flag, Flags, Path}
import scalacache.logging.Logger

import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler, FileLock}
import java.nio.file.StandardOpenOption.{CREATE, READ, WRITE}
import java.nio.file._
import java.util.concurrent.{Executors, TimeUnit}
import scala.compat.java8.FunctionConverters.asJavaConsumer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try
import scalacache.{AbstractCache, Cache}
import scalacache.serialization.Codec
import scalacache.serialization.binary.BinaryCodec

class FileCache[V](prefix: String)(implicit codec: BinaryCodec[V]) extends AbstractCache[IO, String, V] {

  protected def doGet(key: String): IO[Option[V]] = {
    readFile(Path.fromNioPath(path(key)))
      .map(Option.apply)
      .recover { case _: NoSuchFileException => None }
  }


  protected def doPut(key: String, value: V, ttl: Option[Duration]): IO[Unit] = {
    val deleteIfNeeded = ttl.collect { case f: FiniteDuration => f }.fold(IO.unit)(IO.sleep(_) >> doRemove(key))
    writeFile(key, value) >> deleteIfNeeded
  }

  protected def doRemove(key: String): IO[Unit] = {
    fs2.io.file.Files[IO].delete(Path.fromNioPath(path(key))).attempt.void
  }


  protected def doRemoveAll: IO[Unit] = {
    fs2.io.file.Files[IO].deleteRecursively(Path.fromNioPath(Paths.get(prefix))).attempt.void
  }


  def close: IO[Unit] = IO.unit

  protected implicit def F: Sync[IO] = cats.effect.IO.asyncForIO

  override protected final val logger = Logger.getLogger(getClass.getName)

  private def path(key: String) = Paths.get(s"$prefix/$key")

  private def readFile(path: Path): IO[V] = {
    fs2.io.file.Files[IO].readAll(path).compile.to(Array).flatMap(codec.decode _ andThen IO.fromEither)
  }

  private def writeFile(key: String, value: V): IO[Unit] = {
    val filepath = Path.fromNioPath(path(key))
    val flags = Flags(Flag.Write, Flag.Create, Flag.Truncate)
    val write = fs2.io.file.Files[IO].writeAll(filepath)
    val stream = Stream.chunk(Chunk.array(codec.encode(value)))
    write(stream).compile.drain//.flatTap(_ => IO.println(s"wrote value to $filepath"))
  }
}
