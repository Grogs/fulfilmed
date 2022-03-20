package me.gregd.cineworld.dao

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.unsafe.implicits.global
import me.gregd.cineworld.integration.tmdb.TmdbIntegrationService
import me.gregd.cineworld.util.NoOpCache
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

class TmdbIntegrationServiceTest extends AnyFunSuite with Matchers with ScalaFutures with IntegrationPatience with WSClient {

  val tmdb = new TmdbIntegrationService(wsClient, new NoOpCache, new NoOpCache, Stubs.tmdb.config)

  test("fetch imdb id") {
    tmdb.fetchImdbId("419430").unsafeRunSync() shouldBe Some("tt7777777")
    tmdb.fetchImdbId("166426").unsafeRunSync() shouldBe Some("tt1790809")
//    tmdb.fetchImdbId("9999999999").unsafeRunSync() shouldBe None
    //TODO Smoke test
  }

  test("no alternate titles") {
    val size2 = tmdb.alternateTitles("419430").unsafeRunSync().size

    size2 shouldBe 0
  }

  test("Fetch alternate titles") {
    val altTitles = tmdb.alternateTitles("166426").unsafeRunSync()

    altTitles.size shouldBe 4
  }

  test("Fetch now playing") {
    val actual = tmdb.fetchMovies().unsafeRunSync()
    actual.size should be > 10
  }

}
