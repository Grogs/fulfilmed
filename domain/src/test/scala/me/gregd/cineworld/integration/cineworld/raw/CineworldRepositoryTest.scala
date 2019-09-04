package me.gregd.cineworld.integration.cineworld.raw

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.domain.transformer.CineworldTransformer
import me.gregd.cineworld.integration.cineworld.CineworldIntegrationService
import me.gregd.cineworld.util.{NoOpCache, RealClock}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

class CineworldRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers with WSClient {

  val cineworld = new CineworldIntegrationService(wsClient, NoOpCache.cache, Stubs.cineworld.config, RealClock)

  test("retrieveCinemas") {
    val cinemas = cineworld.retrieveCinemas().futureValue

    val (london, rest) = cinemas
      .map(CineworldTransformer.toCinema(_, None))
      .partition(_.name.startsWith("London - "))

    london should not be empty
    rest should not be empty
  }

  test("retrieve7DayListings") {
    val listings = cineworld.retrieveListings("8112", LocalDate.now()).futureValue
    listings.films should not be empty
    listings.events should not be empty
  }

}
