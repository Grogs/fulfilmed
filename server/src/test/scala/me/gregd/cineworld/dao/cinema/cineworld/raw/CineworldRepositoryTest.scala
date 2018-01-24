package me.gregd.cineworld.dao.cinema.cineworld.raw

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.util.NoOpCache
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import stub.Stubs._

class CineworldRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val cineworld = new CineworldRepository(wsClient, NoOpCache.cache, Stubs.cineworld.config)

  test("retrieveCinemas") {
    val cinemas = cineworld.retrieveCinemas().futureValue

    val (london, rest) = cinemas
      .map(CineworldRepositoryTransformer.toCinema)
      .partition(_.name.startsWith("London - "))

    london should not be empty
    rest should not be empty
  }

  test("retrieve7DayListings") {
    cineworld.retrieve7DayListings("1010882").futureValue should not be empty
  }

}
