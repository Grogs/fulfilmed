package me.gregd.cineworld.util

import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class RateLimiterTest extends FunSuite with Matchers {

  test("rate limit 100 per second") {
    val start = System.currentTimeMillis()

    def task(i: Int) = Future.successful(println(i + ": " + (System.currentTimeMillis() - start)))

    val rateLimit = RateLimiter(100.millis, 5)

    val results = for (i <- 1 to 24) yield rateLimit(task(i))
    results.foreach(Await.result(_, 5.seconds))

    val elapsed = System.currentTimeMillis() - start

    elapsed should be > 400L
    elapsed should be < 800L
  }
}
