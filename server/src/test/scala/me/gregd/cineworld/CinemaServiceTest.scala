package me.gregd.cineworld

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.{FakeRatings, NoOpCache}
import me.gregd.cineworld.config.values.{TmdbKey, TmdbRateLimit}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import me.gregd.cineworld.dao.cinema.vue.VueCinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain.Coordinates
import me.gregd.cineworld.util.FixedClock
import monix.execution.Scheduler
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

import scala.concurrent.duration._

class CinemaServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(3000, Millis))

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val tmdb = new TheMovieDB(TmdbKey(""), wsClient, Stubs.tmdb.baseUrl, NoOpCache.cache, Scheduler.global, TmdbRateLimit(1.second, 1000))
  val repo = new VueRepository(wsClient, NoOpCache.cache, Stubs.vue.baseUrl)

  val movieDao = new Movies(tmdb, FakeRatings)
  val cineworldRaw = new CineworldRepository(wsClient, NoOpCache.cache, Stubs.cineworld.baseUrl)
  val cineworldDao = new CineworldCinemaDao(movieDao, tmdb, cineworldRaw)
  val clock = FixedClock(LocalDate.parse("2017-05-23"))
  val vueDao = new VueCinemaDao(repo, movieDao, clock)

  val cinemaService = new CinemaService(movieDao, cineworldDao, vueDao, clock)


  test("getMoviesAndPerformances") {

    val res = cinemaService.getMoviesAndPerformances("1010882", "today").futureValue.toSeq

    res.size shouldBe 11

    val Some((movie, performances)) = res.find(_._1.title == "Guardians Of The Galaxy Vol. 2")

    performances.size shouldBe 4
  }

  test("getCinemas") {

    val res = cinemaService.getCinemas().futureValue.toMap

    res.keys.toList shouldBe List("Cineworld", "Vue")

    val cineworld = res("Cineworld")
    val vue = res("Vue")

    cineworld.size shouldBe 2
    vue.size shouldBe 2
  }

  test("getNearbyCinemas") {

    val res = cinemaService.getNearbyCinemas(Coordinates(50,0)).futureValue
    val nearbyCinemaNames = res.map(_.name)

    nearbyCinemaNames shouldBe Seq("Cineworld - Brighton (90.7 km)", "Cineworld - Eastbourne (91.3 km)", "Cineworld - Chichester (107.8 km)", "Cineworld - Isle Of Wight (120.1 km)", "Cineworld - Crawley (125.3 km)", "Cineworld - Whiteley (132.4 km)", "Cineworld - Southampton (140.3 km)", "Cineworld - Ashford (143.1 km)", "Cineworld - Aldershot (149.2 km)", "Cineworld - Bromley (156.5 km)")
  }
}
