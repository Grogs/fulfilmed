package me.gregd.cineworld.dao.cinema.vue

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.FakeRatings
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.movies.Movies
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
  val tmdb = new TheMovieDB(wsClient, NoOpCache.cache, Scheduler.global, Stubs.tmdb.config)
  val repo = new VueRepository(wsClient, NoOpCache.cache, Stubs.vue.config)
  val movieDao = new Movies(tmdb, FakeRatings)
  val dao = new VueCinemaDao(repo, movieDao, clock)

  test("retrieveCinemas") {
    dao.retrieveCinemas().futureValue should not be empty
  }

  test("retrieveMoviesAndPerformances for 1010882") {
    val listings = dao.retrieveMoviesAndPerformances("10032", clock.today()).futureValue
    val Some((movie, performances)) = listings.find(_._1.tmdbId.isDefined)

    movie.title should not be empty
    movie.posterUrl should not be empty
    movie.tmdbId should not be empty
    movie.imdbId should not be empty

    performances should not be empty
  }
}
