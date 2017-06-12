package me.gregd.cineworld.dao

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.NoOpCache
import me.gregd.cineworld.config.values.TmdbKey
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

class TheMovieDBTest extends FunSuite with Matchers with ScalaFutures with IntegrationPatience {

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val tmdb = new TheMovieDB(TmdbKey(""), wsClient, Stubs.tmdb.baseUrl, NoOpCache.cache)

  test("fetch imdb id") {
    tmdb.fetchImdbId("419430").futureValue shouldBe Some("tt7777777")
//    tmdb.fetchImdbId("9999999999").futureValue shouldBe None
    //TODO Smoke test
  }

  ignore("Fetch alternate title") {
    val size2 = tmdb.alternateTitles("419430").futureValue.size

    size2 shouldBe 0
  }

  test("baseImageUrl") {
    tmdb.baseImageUrl should include("image.tmdb.org")
  }

  test("Fetch now playing") {
    val actual = tmdb.fetchNowPlaying().futureValue
    actual.size should be > 10
  }

}
