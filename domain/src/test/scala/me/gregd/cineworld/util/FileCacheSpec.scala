package me.gregd.cineworld.util

import cats.effect.IO
import cats.implicits.toTraverseOps

import java.nio.file.Files
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.duration._
import cats.effect.unsafe.implicits.global
import fs2.io.file.Path

import scala.language.postfixOps

class FileCacheSpec extends AnyFunSuite with Matchers with Eventually with IntegrationPatience with BeforeAndAfterEach {

  val cacheDir = Files.createTempDirectory("FileCacheSpec")
  val cache = new FileCache[Int](cacheDir.toString)
  val files = fs2.io.file.Files[IO].list(Path.fromNioPath(cacheDir)).compile.toList

  override def afterEach(): Unit = {
    super.afterEach()
    cache.removeAll.unsafeRunSync()
  }

  test("remove") {
    for {
      initialFiles <- files
      _ <- cache.put("a")(1, None)
      _ <- cache.put("b")(2, None)
      filesAfterPut <- files
      _ <- cache.remove("c")
      filesAfterRemoveUnusedKey <- files
      _ <- cache.remove("b")
      filesAfterRemoveB <- files
    } yield {
      initialFiles shouldBe empty
      filesAfterPut.size shouldBe 2
      filesAfterRemoveUnusedKey.size shouldBe 2
      filesAfterRemoveB.size shouldBe 1
    }
  }

  test("put without ttl") {
    for {
      initialFiles <- files
      _ <- cache.put("someKey")(1, None)
      filesAfterPut <- files
    } yield {
      initialFiles shouldBe empty
      filesAfterPut.size shouldBe 1
    }
  }

  test("put with ttl") {
    for {
      initialFiles <- files
      _ <- cache.put("someKey")(42, Option(300.millis))
      filesAfterPut <- files
      filesAfterTtlElapsed <- IO.sleep(301.millis) >> files
    } yield {
      initialFiles shouldBe empty
      filesAfterPut.size shouldBe 1
      filesAfterTtlElapsed shouldBe empty
    }
  }

  test("removeAll") {
    for {
      initialFiles <- files
      _ <- cache.put("a")(1, None)
      _ <- cache.put("b")(2, None)
      _ <- cache.put("c")(3, None)
      _ <- cache.put("d")(4, None)
      filesAfterPuts <- files
      _ <- cache.removeAll
      filesAfterRemoveAll <- files
    } yield {
      initialFiles shouldBe empty
      filesAfterPuts.size shouldBe 4
      filesAfterRemoveAll shouldBe empty
    }
  }

  test("get") {
    for {
      getBeforePut <- cache.get("someKey")
      _ <- cache.put("someKey")(42, None)
      getAfterPut <- cache.get("someKey")
    } yield {
      getBeforePut shouldBe None
      getAfterPut shouldBe Some(42)
    }
  }
}
