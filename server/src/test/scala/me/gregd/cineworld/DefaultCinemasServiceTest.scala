package me.gregd.cineworld

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.wiring.MoviesConfig
import me.gregd.cineworld.domain.Coordinates
import me.gregd.cineworld.util.{FixedClock, NoOpCache}
import me.gregd.cineworld.wiring.{Config, DomainWiring, IntegrationWiring, MoviesConfig}
import monix.execution.Scheduler
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

import scala.concurrent.duration._

class DefaultCinemasServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(5000, Millis))

  val date = LocalDate.parse("2017-05-23")

  val fakeClock = FixedClock(date)

  val config = Config(Stubs.omdb.config, Stubs.tmdb.config, Stubs.cineworld.config, Stubs.vue.config, Stubs.postcodesio.config, MoviesConfig(1.second))

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))

  val integrationWiring = new IntegrationWiring(wsClient, NoOpCache.cache, fakeClock, Scheduler.global, config)

  val domainWiring = new DomainWiring(fakeClock, config, integrationWiring)

  val cinemaService = domainWiring.cinemaService
  val listingService = domainWiring.listingService

  test("getMoviesAndPerformances") { //TODO split into separate test

    val res = listingService.getMoviesAndPerformancesFor("10032", date.toString).futureValue.toSeq

    res.size shouldBe 10

    val Some((movie, performances)) = res.find(_._1.title == "Guardians Of The Galaxy Vol. 2")

    performances.size shouldBe 5
  }

  test("getCinemasGrouped") {

    val res = cinemaService.getCinemasGrouped().futureValue.toMap

    res.keys.toList shouldBe List("Cineworld", "Vue")

    val cineworld = res("Cineworld")
    val vue = res("Vue")

    cineworld.size shouldBe 2
    vue.size shouldBe 2
  }

  test("getNearbyCinemas") {

    val res = cinemaService.getNearbyCinemas(Coordinates(51.513605, -0.128382)).futureValue
    val nearbyCinemaNames = res.map(_.name)

    nearbyCinemaNames shouldBe Seq(
      "Cineworld - Aldershot (53.3 km)",
      "Vue - Bury - The Rock (273.9 km)",
      "Cineworld - Aberdeen - Union Square (639.1 km)",
      "Cineworld - Aberdeen - Queens Links (639.5 km)"
    )
  }
}
