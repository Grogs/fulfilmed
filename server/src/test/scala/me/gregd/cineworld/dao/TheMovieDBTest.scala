package me.gregd.cineworld.dao

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.NoOpCache
import me.gregd.cineworld.config.values.{TmdbKey, TmdbRateLimit}
import monix.execution.Scheduler
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import scala.concurrent.duration._

class TheMovieDBTest extends FunSuite with Matchers with ScalaFutures with IntegrationPatience {

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val tmdb = new TheMovieDB(TmdbKey(""), wsClient, Stubs.tmdb.baseUrl, NoOpCache.cache, Scheduler.global, TmdbRateLimit(1.second, 1000))

  test("fetch imdb id") {
    tmdb.fetchImdbId("419430").futureValue shouldBe Some("tt7777777")
    tmdb.fetchImdbId("166426").futureValue shouldBe Some("tt1790809")
//    tmdb.fetchImdbId("9999999999").futureValue shouldBe None
    //TODO Smoke test
  }

  test("no alternate titles") {
    val size2 = tmdb.alternateTitles("419430").futureValue.size

    size2 shouldBe 0
  }

  test("Fetch alternate titles") {
    val altTitles = tmdb.alternateTitles("166426").futureValue

    altTitles.size shouldBe 4
  }

  test("baseImageUrl") {
    tmdb.baseImageUrl should include("image.tmdb.org")
  }

  test("Fetch now playing") {
    val actual = tmdb.fetchMovies().futureValue
    actual.size should be > 10
  }

}
