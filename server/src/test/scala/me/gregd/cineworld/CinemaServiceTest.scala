package me.gregd.cineworld

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.{FakeCineworldRepository, FakeRatings, NoOpCache}
import me.gregd.cineworld.config.values.{TmdbKey, TmdbRateLimit}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.cinema.vue.VueCinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.util.FixedClock
import monix.execution.Scheduler
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import scala.concurrent.duration._

class CinemaServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(1500, Millis))

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val tmdb = new TheMovieDB(TmdbKey(""), wsClient, Stubs.tmdb.baseUrl, NoOpCache.cache, Scheduler.global, TmdbRateLimit(1.second, 1000))
  val repo = new VueRepository(wsClient, NoOpCache.cache, Stubs.vue.baseUrl)

  val movieDao = new Movies(tmdb, FakeRatings)
  val cineworldDao = new CineworldCinemaDao(movieDao, tmdb, FakeCineworldRepository)
  val clock = FixedClock(LocalDate.parse("2017-03-23"))
  val vueDao = new VueCinemaDao(repo, movieDao, clock)

  val cinemaService = new CinemaService(movieDao, cineworldDao, vueDao, clock)


  test("testGetMoviesAndPerformances") {

    val res = cinemaService.getMoviesAndPerformances("1010882", "today").futureValue.toSeq

    res.size shouldBe 2

    val (movie, performances) = res.head

    movie.title shouldBe "Beauty And The Beast"

    performances.size shouldBe 2
  }

}
