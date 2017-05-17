package me.gregd.cineworld

import java.time.LocalDate

import fakes.{FakeCineworldRepository, FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.util.FixedClock
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by grogs on 17/07/2016.
  */
class CinemaServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(1500, Millis))

  val movieDao = new Movies(FakeTheMovieDB, FakeRatings)
  val clock = FixedClock(LocalDate.parse("2017-03-23"))
  val cinemaDao = new CineworldCinemaDao(movieDao, FakeTheMovieDB, FakeCineworldRepository, clock)

  val cinemaService = new CinemaService(movieDao, cinemaDao, clock)


  test("testGetMoviesAndPerformances") {

    val res = cinemaService.getMoviesAndPerformances("1010882", "today").futureValue.toSeq

    res.size shouldBe 2

    val (movie, performances) = res.head

    movie.title shouldBe "Beauty And The Beast"

    performances.size shouldBe 2
  }

}
