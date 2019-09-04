package me.gregd.cineworld.domain.movies

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.integration.omdb.{OmdbIntegrationService, RatingsResult}
import me.gregd.cineworld.util.NoOpCache
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

class OmdbIntegrationServiceTest extends FunSuite  with Matchers with ScalaFutures with IntegrationPatience with WSClient {

  val ratings = new OmdbIntegrationService(wsClient, NoOpCache.cache, Stubs.omdb.config)

  test("get known ratings") {
    val RatingsResult(rating, votes, metascore, rottenTomatoes) = ratings.fetchRatings("tt0451279").futureValue

    rating shouldBe Some(8.3)
    votes shouldBe Some(61125)
    metascore shouldBe Some(76)
    rottenTomatoes shouldBe Some("93%")
  }

  test("ratings for invalid ID") {
    val RatingsResult(rating, votes, metascore, rottenTomatoes) = ratings.fetchRatings("invalid").futureValue

    rating shouldBe None
    votes shouldBe None
    metascore shouldBe None
    rottenTomatoes shouldBe None
  }

  test("ratings for movie without any ratings") {
    val RatingsResult(rating, votes, metascore, rottenTomatoes) = ratings.fetchRatings("tt0974015").futureValue

    rating shouldBe None
    votes shouldBe None
    metascore shouldBe None
    rottenTomatoes shouldBe None
  }

}
