package me.gregd.cineworld.dao.cineworld

import org.scalatest.FunSuite
import org.scalatest.concurrent.Futures
import play.api.libs.ws.ahc.AhcWSClient
import play.api.test.FakeApplication

import scala.concurrent._
import scala.concurrent.duration._


class CineworldDaoIT extends FunSuite with Futures {

  implicit val ec = ExecutionContext.global

  val fakeApp = FakeApplication()

  val cineworldDao = fakeApp.injector.instanceOf[CineworldDao]

  test("testRetrieveCinemas") {
    println(Await.result(cineworldDao.retrieveCinemas(), 10.seconds))
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
