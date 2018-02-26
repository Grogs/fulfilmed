package me.gregd.cineworld

import java.time.LocalDate

import me.gregd.cineworld.domain.Coordinates
import me.gregd.cineworld.util.FixedClock
import me.gregd.cineworld.wiring.TestAppWiring
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}

class CinemaServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(3000, Millis))

  val date = LocalDate.parse("2017-05-23")

  val fakeClock = FixedClock(date)

  val cinemaService = new TestAppWiring(fakeClock).cinemaService

  test("getMoviesAndPerformances") {

    val res = cinemaService.getMoviesAndPerformances("1010882", date).futureValue.toSeq

    res.size shouldBe 11

    val Some((movie, performances)) = res.find(_._1.title == "Guardians Of The Galaxy Vol. 2")

    performances.size shouldBe 4
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

    val res = cinemaService.getNearbyCinemas(Coordinates(50,0)).futureValue
    val nearbyCinemaNames = res.map(_.name)

    nearbyCinemaNames shouldBe Seq("Cineworld - Brighton (90.7 km)", "Cineworld - Eastbourne (91.3 km)", "Cineworld - Chichester (107.8 km)", "Cineworld - Isle Of Wight (120.1 km)", "Cineworld - Crawley (125.3 km)", "Cineworld - Ashford (143.1 km)", "Cineworld - Aldershot (149.2 km)", "Cineworld - Bromley (156.5 km)", "Cineworld - Rochester (157.1 km)", "Cineworld - London - Bexleyheath (162.3 km)")
  }
}
