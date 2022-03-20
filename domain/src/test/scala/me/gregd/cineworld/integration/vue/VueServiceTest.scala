package me.gregd.cineworld.integration.vue

import java.time.LocalDate
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.unsafe.implicits.global
import me.gregd.cineworld.domain.service.VueService
import me.gregd.cineworld.integration.tmdb.TmdbIntegrationService
import me.gregd.cineworld.integration.vue.VueIntegrationService
import me.gregd.cineworld.util.{FixedClock, NoOpCache}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

class VueServiceTest extends AnyFunSuite with ScalaFutures with IntegrationPatience with Matchers with WSClient {

  val date: LocalDate = LocalDate.parse("2017-05-23")
  val clock = FixedClock(date)
  val tmdb = new TmdbIntegrationService(wsClient, new NoOpCache, new NoOpCache, Stubs.tmdb.config)
  val repo = new VueIntegrationService(wsClient, new NoOpCache, Stubs.vue.config)
  val dao = new VueService(repo, clock)

  test("retrieveCinemas") {
    dao.retrieveCinemas().unsafeRunSync() should not be empty
  }

  test("retrieveMoviesAndPerformances for 1010882") {
    val listings = dao.retrieveMoviesAndPerformances("10032", clock.today()).unsafeRunSync()
    val (movie, performances) = listings.head

    movie.title should not be empty
    movie.id should not be empty
    movie.poster_url should not be empty

    performances should not be empty
  }
}
