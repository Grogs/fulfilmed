package me.gregd.cineworld.domain

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.util.{FixedClock, NoOpCache}
import me.gregd.cineworld.wiring._
import monix.execution.Scheduler
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

import scala.concurrent.duration._

class CompositeCinemaServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(5000, Millis))

  val date = LocalDate.parse("2017-05-23")

  val fakeClock = FixedClock(date)

  //todo
//  val config = Config()
//
//  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
//
//  val integrationWiring = new IntegrationWiring(wsClient, NoOpCache.cache, fakeClock, Scheduler.global)(Stubs.omdb.config, Stubs.tmdb.config, Stubs.cineworld.config, Stubs.vue.config, Stubs.postcodesio.config, MoviesConfig(1.second), DatabaseConfig("jdbc:sqlite:test.db"))
//
//  val domainWiring = new DomainServiceWiring(fakeClock, config, integrationWiring)
//
//  val cinemaService = domainWiring.cinemaService
//  val listingService = domainWiring.listingService
//
//  test("getMoviesAndPerformances") { //TODO split into separate test
//
//    val res = listingService.getMoviesAndPerformances("10032", date).futureValue.toSeq
//
//    res.size shouldBe 10
//
//    val Some((movie, performances)) = res.find(_._1.title == "Guardians Of The Galaxy Vol. 2")
//
//    performances.size shouldBe 5
//  }

}
