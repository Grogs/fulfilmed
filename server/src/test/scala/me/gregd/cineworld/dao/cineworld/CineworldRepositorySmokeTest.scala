package me.gregd.cineworld.dao.cineworld

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.test.FakeApplication

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class CineworldRepositorySmokeTest
    extends FunSuite
    with ScalaFutures
    with IntegrationPatience
    with Matchers {

  val fakeApp = FakeApplication()

  val cineworldDao = fakeApp.injector.instanceOf[CineworldRepository]

  test("testRetrieveCinemas") {
    val cinemas = cineworldDao.retrieveCinemas().futureValue

    val (london, rest) = cinemas
      .map(CineworldRepository.toCinema)
      .partition(_.name.startsWith("London - "))

    london should not be empty
    rest should not be empty
  }

  test("testRetrieve7DayListings") {
    val listings = for {
      cinemas <- cineworldDao.retrieveCinemas()
      someId = cinemas.head.id.toString
      listings <- cineworldDao.retrieve7DayListings(someId)
    } yield listings

    listings.futureValue should not be empty
  }

}
