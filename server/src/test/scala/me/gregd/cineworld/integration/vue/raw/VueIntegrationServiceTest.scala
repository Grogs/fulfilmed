package me.gregd.cineworld.dao.cinema.vue.raw

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.integration.vue.VueIntegrationService
import me.gregd.cineworld.integration.vue.cinemas.VueCinema
import me.gregd.cineworld.util.NoOpCache
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

class VueIntegrationServiceTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers with JsonMatchers {

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val vue = new VueIntegrationService(wsClient, NoOpCache.cache, Stubs.vue.config)

  test("curlCinemas") {
    val response = vue.curlCinemas().futureValue
    response shouldBe validJson
  }

  test("curlListings") {
    val response = vue.curlListings("10032").futureValue
    response shouldBe validJson
  }

  test("retrieveCinemas") {
    val vueCinemas = vue.retrieveCinemas().futureValue
    vueCinemas should not be empty
  }

  test("retrieveListings") {
    val vueListings = vue.retrieveListings("10032").futureValue
    vueListings.films should not be empty
  }

  test("retrieveLocation") {
    val location = vue.retrieveLocation(VueCinema("Bury The Rock", "", "", "", "", "", false)).futureValue
    location shouldBe Option((53.594033d,-2.296314d))
  }

}
