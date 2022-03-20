package me.gregd.cineworld.domain.movies

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.unsafe.implicits.global
import me.gregd.cineworld.integration.omdb.{OmdbIntegrationService, RatingsResult}
import me.gregd.cineworld.util.NoOpCache
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

class OmdbIntegrationServiceTest extends AnyFunSuite  with Matchers with WSClient {

  val ratings = new OmdbIntegrationService(wsClient, new NoOpCache, Stubs.omdb.config)

  test("get known ratings") {
    val RatingsResult(rating, votes, metascore, rottenTomatoes) = ratings.fetchRatings("tt0451279").unsafeRunSync()

    rating shouldBe Some(8.3)
    votes shouldBe Some(61125)
    metascore shouldBe Some(76)
    rottenTomatoes shouldBe Some("93%")
  }

  test("ratings for invalid ID") {
    val RatingsResult(rating, votes, metascore, rottenTomatoes) = ratings.fetchRatings("invalid").unsafeRunSync()

    rating shouldBe None
    votes shouldBe None
    metascore shouldBe None
    rottenTomatoes shouldBe None
  }

  test("ratings for movie without any ratings") {
    val RatingsResult(rating, votes, metascore, rottenTomatoes) = ratings.fetchRatings("tt0974015").unsafeRunSync()

    rating shouldBe None
    votes shouldBe None
    metascore shouldBe None
    rottenTomatoes shouldBe None
  }

}
