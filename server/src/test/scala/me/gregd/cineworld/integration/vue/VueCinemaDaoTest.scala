package me.gregd.cineworld.dao.cinema.vue

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.gregd.cineworld.domain.VueCinemaDao
import me.gregd.cineworld.integration.tmdb.TmdbService
import me.gregd.cineworld.integration.vue.VueService
import me.gregd.cineworld.util.{FixedClock, NoOpCache}
import monix.execution.Scheduler
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

class VueCinemaDaoTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  val date: LocalDate = LocalDate.parse("2017-05-23")
  val clock = FixedClock(date)
  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val tmdb = new TmdbService(wsClient, NoOpCache.cache, Scheduler.global, Stubs.tmdb.config)
  val repo = new VueService(wsClient, NoOpCache.cache, Stubs.vue.config)
  val dao = new VueCinemaDao(repo, clock)

  test("retrieveCinemas") {
    dao.retrieveCinemas().futureValue should not be empty
  }

  test("retrieveMoviesAndPerformances for 1010882") {
    val listings = dao.retrieveMoviesAndPerformances("10032", clock.today()).futureValue
    val (movie, performances) = listings.head

    movie.title should not be empty
    movie.id should not be empty
    movie.poster_url should not be empty

    performances should not be empty
  }
}
