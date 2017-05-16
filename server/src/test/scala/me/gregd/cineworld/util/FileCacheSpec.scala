package me.gregd.cineworld.util

import java.nio.file.Files

import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}

import scala.collection.JavaConverters._
import scalacache.AnyRefBinaryCodec
import scala.concurrent.duration._

class FileCacheSpec extends FunSuite with Matchers with ScalaFutures with Eventually with IntegrationPatience{

  test("remove") {
    val cacheDir = Files.createTempDirectory("FileCacheSpec").toAbsolutePath
    val cache = new FileCache(cacheDir.toString)
    def files = Files.list(cacheDir).iterator().asScala.toList

    files shouldBe empty

    cache.put("a", 1, None)
    cache.put("b", 2, None)

    eventually(timeout(1.second)) {
      files.size shouldBe 2
    }

    cache.remove("c").futureValue
    files.size shouldBe 2
    cache.remove("b").futureValue
    files.size shouldBe 1
  }

  test("put without ttl") {
    val cacheDir = Files.createTempDirectory("FileCacheSpec").toAbsolutePath
    val cache = new FileCache(cacheDir.toString)
    def files = Files.list(cacheDir).iterator().asScala.toList

    files shouldBe empty
    cache.put("someKey", 1, None).futureValue shouldBe (())
    files.size shouldBe 1
  }

  test("put with ttl") {
    val cacheDir = Files.createTempDirectory("FileCacheSpec").toAbsolutePath
    val cache = new FileCache(cacheDir.toString)
    def files = Files.list(cacheDir).iterator().asScala.toList

    files shouldBe empty

    val write = System.currentTimeMillis()
    cache.put("someKey", 42, Option(300.millis)).futureValue shouldBe (())
    val written = System.currentTimeMillis()

    files.size shouldBe 1

    eventually(timeout(1.second)){
      files.size shouldBe 0
      val timeLived = System.currentTimeMillis() - write
      timeLived shouldBe > (300L)
      timeLived shouldBe < (600L)
    }
  }

  test("removeAll") {
    val cacheDir = Files.createTempDirectory("FileCacheSpec").toAbsolutePath
    val cache = new FileCache(cacheDir.toString)
    def files = Files.list(cacheDir).iterator().asScala.toList

    files shouldBe empty

    cache.put("a", 1, None)
    cache.put("b", 2, None)
    cache.put("c", 3, None)
    cache.put("d", 4, None)

    eventually(timeout(1.second)) {
      files.size shouldBe 4
    }

    cache.removeAll()

    eventually(timeout(1.second)) {
      files.size shouldBe 4
    }
  }

  test("get") {
    val cacheDir = Files.createTempDirectory("FileCacheSpec").toAbsolutePath
    val cache = new FileCache(cacheDir.toString)
    def files = Files.list(cacheDir).iterator().asScala.toList

    cache.get[Int]("someKey").futureValue shouldBe None

    cache.put("someKey", 42, None).futureValue

    cache.get[Int]("someKey").futureValue shouldBe Some(42)
    cache.get[String]("someKey").failed.futureValue shouldBe a [Throwable]


  }

  test("close") {
    val cacheDir = Files.createTempDirectory("FileCacheSpec").toAbsolutePath
    val cache = new FileCache(cacheDir.toString)
    def files = Files.list(cacheDir).iterator().asScala.toList

    //Queue up some async actions
    cache.put("a", 1, None)
    cache.put("b", 2, None)
    cache.put("c", 3, None)
    cache.put("d", 4, None)
    cache.removeAll()

    //Close should return after removeAll is processed
    cache.close()

    files shouldBe empty
  }

}
