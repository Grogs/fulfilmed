package me.gregd.cineworld.web

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.domain.model.Coordinates
import me.gregd.cineworld.util.{FixedClock, NoOpCache}
import me.gregd.cineworld.web.service.DefaultCinemaService
import me.gregd.cineworld.wiring._
import monix.execution.Scheduler
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

import scala.concurrent.duration._

class DefaultCinemaServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(5000, Millis))

  val date = LocalDate.parse("2017-05-23")

  val fakeClock = FixedClock(date)

  val config = Config(Stubs.omdb.config, Stubs.tmdb.config, Stubs.cineworld.config, Stubs.vue.config, Stubs.postcodesio.config, MoviesConfig(1.second), DatabaseConfig("test"))

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))

  val integrationWiring = new IntegrationWiring(wsClient, NoOpCache.cache, fakeClock, Scheduler.global, config)

  val domainWiring = new DomainWiring(fakeClock, config, integrationWiring)

  val cinemaService = new DefaultCinemaService(domainWiring.cinemaService)

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
