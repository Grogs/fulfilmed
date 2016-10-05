package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.Cinema
import org.scalatest.FunSuite
import org.scalatest.concurrent.Futures
import play.api.libs.ws.ahc.AhcWSClient
import play.api.test.FakeApplication

import scala.concurrent._
import scala.concurrent.duration._


class CineworldDaoIT extends FunSuite with Futures {

  implicit val ec = ExecutionContext.global

  val fakeApp = FakeApplication()

  val cineworldDao = fakeApp.injector.instanceOf[CineworldRepository]

  test("testRetrieveCinemas") {
    val cinemas = Await.result(cineworldDao.retrieveCinemas(), 10.seconds)
    val (london, rest) = cinemas.map(CineworldRepository.toCinema).partition(_.name.startsWith("London - "))
    println("London")
    def print(c: Cinema) = println(s"""Cinema("${c.id}", "${c.name}")""")
    london.foreach(print)
    println("Rest")
    rest.foreach(print)
  }

  test("testRetrieve7DayListings") {
    val listings = for {
      cinemas <- cineworldDao.retrieveCinemas()
      someId = cinemas.head.id.toString
      listings <- cineworldDao.retrieve7DayListings(someId)
    } yield listings
    println(Await.result(listings, 10.seconds))
  }

}
