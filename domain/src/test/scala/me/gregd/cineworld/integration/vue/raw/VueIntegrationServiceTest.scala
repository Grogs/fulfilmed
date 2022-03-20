package me.gregd.cineworld.integration.vue.raw

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.unsafe.implicits.global
import me.gregd.cineworld.integration.vue.VueIntegrationService
import me.gregd.cineworld.integration.vue.cinemas.VueCinema
import me.gregd.cineworld.util.NoOpCache
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

class VueIntegrationServiceTest extends AnyFunSuite with ScalaFutures with IntegrationPatience with Matchers with JsonMatchers with WSClient {

  val vue = new VueIntegrationService(wsClient, new NoOpCache, Stubs.vue.config)

  test("curlCinemas") {
    val response = vue.curlCinemas().unsafeRunSync()
    response shouldBe validJson
  }

  test("curlListings") {
    val response = vue.curlListings("10032").unsafeRunSync()
    response shouldBe validJson
  }

  test("retrieveCinemas") {
    val vueCinemas = vue.retrieveCinemas().unsafeRunSync()
    vueCinemas should not be empty
  }

  test("retrieveListings") {
    val vueListings = vue.retrieveListings("10032").unsafeRunSync()
    vueListings.films should not be empty
  }

  test("retrieveLocation") {
    val location = vue.retrieveLocation(VueCinema("Bury The Rock", "", "", "", "", "", hidden = false)).unsafeRunSync()
    location shouldBe Option((53.594033d,-2.296314d))
  }

}
