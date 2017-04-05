package me.gregd.cineworld.dao.cineworld

import fakes.FakeTheMovieDB
import me.gregd.cineworld.dao.TheMovieDB
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global

class CineworldRepositorySmokeTest
    extends FunSuite
    with ScalaFutures
    with IntegrationPatience
    with Matchers {

  val app = new GuiceApplicationBuilder().overrides(bind[TheMovieDB].toInstance(FakeTheMovieDB)).build

  val cineworldDao = app.injector.instanceOf[CineworldRepository]

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
