package me.gregd.cineworld.dao.cinema.cineworld.raw

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import stub.Stubs._

class CineworldRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  test("retrieveCinemas") {
    withStubbedCineworld { cineworld =>
      val cinemas = cineworld.retrieveCinemas().futureValue

      val (london, rest) = cinemas
        .map(CineworldRepositoryTransformer.toCinema)
        .partition(_.name.startsWith("London - "))

      london should not be empty
      rest should not be empty
    }
  }

  test("retrieve7DayListings") {
    withStubbedCineworld { cineworld =>
      cineworld.retrieve7DayListings("1010882").futureValue should not be empty
    }
  }


}
