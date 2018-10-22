package me.gregd.cineworld.util

import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler, FileLock}
import java.nio.file.StandardOpenOption.{CREATE, READ, WRITE}
import java.nio.file._
import java.util.concurrent.{Executors, TimeUnit}

import scala.compat.java8.FunctionConverters.asJavaConsumer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try
import scalacache.Cache
import scalacache.serialization.Codec

class FileCache(prefix: String) extends Cache[Array[Byte]] {

  private val scheduler = Executors.newScheduledThreadPool(2)
  private val ec = ExecutionContext.fromExecutor(scheduler)

  override def get[V](key: String)(implicit codec: Codec[V, Array[Byte]]): Future[Option[V]] = {
    Future(AsynchronousFileChannel.open(path(key), READ))(ec).flatMap{
      channel =>
        readFile(codec, channel)
          .map(Option.apply)
    }.recover { case _: NoSuchFileException => None }
  }

  /**
    * Note: At it stands, if the process is restarted, entries with a TTL won't get deleted :(
    * Future work: maybe store TTL and creation time, then remove during get.
    */
  override def put[V](key: String, value: V, ttl: Option[Duration])(implicit codec: Codec[V, Array[Byte]]): Future[Unit] = {
    val res = writeFile(key, value, codec)
    ttl.foreach { ttl =>
      val delete = runnable {
        val location = path(key)
        Try(
          Files.delete(location)
        ).recover { case e => e.printStackTrace() }
      }
      scheduler.schedule(delete, ttl.toMillis, TimeUnit.MILLISECONDS)
    }
    res
  }


  override def remove(key: String): Future[Unit] = {
    Future(Files.delete(path(key)))(ec).recover{ case _ => () }
  }

  override def removeAll(): Future[Unit] = {
    val completion = Promise[Unit]()

    def deleteAll = {
      Files.list(Paths.get(prefix)).forEach(
        asJavaConsumer(item =>
          Files.delete(item)
        )
      )
    }

    val deleteAndComplete = runnable {
      completion.complete(Try(deleteAll))
    }
    scheduler.execute(deleteAndComplete)
    completion.future
  }

  override def close(): Unit = {
    scheduler.shutdown()
    scheduler.awaitTermination(1, TimeUnit.MINUTES)
  }

  private def path(key: String) = Paths.get(s"$prefix/$key")

  private def readFile[V](codec: Codec[V, Array[Byte]], channel: AsynchronousFileChannel): Future[V] = {
    for {
      fileSize <- Future.successful(channel.size().toInt) //Will fail if file is larger than 2.147 gigabytes
      buffer = ByteBuffer.allocate(fileSize)
      _ <- read(channel, buffer)
      bytes = buffer.array()
      value = codec.deserialize(bytes)
    } yield value
  }

  private def lock[V](channel: AsynchronousFileChannel) = {
    val lock = Promise[FileLock]()

    channel.lock((), completionHandler(lock))

    lock.future
  }

  private def read(channel: AsynchronousFileChannel, buffer: ByteBuffer): Future[Integer] = {
    val completion = Promise[Integer]()

    channel.read(buffer, 0, (), completionHandler(completion))

    completion.future
  }


  private def writeFile[V](key: String, value: V, codec: Codec[V, Array[Byte]]) = {
    val completion = Promise[Integer]()
    val channel = AsynchronousFileChannel.open(path(key), CREATE, WRITE)
    val bytes = codec.serialize(value)
    val buffer = ByteBuffer.wrap(bytes)
    val handler = completionHandler(completion)
    channel.write(buffer, 0, (), handler)
    completion.future.map { _ =>
      channel.close()
      ()
    }
  }

  private def completionHandler[V](promise: Promise[V]) = {
    new CompletionHandler[V, Unit] {
      def failed(exc: Throwable, attachment: Unit): Unit = promise.failure(exc)

      def completed(result: V, attachment: Unit): Unit = promise.success(result)
    }
  }

  private def runnable(f: => Unit) = new Runnable {
    def run(): Unit = f
  }
}
