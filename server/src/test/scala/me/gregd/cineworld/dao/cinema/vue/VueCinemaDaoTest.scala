package me.gregd.cineworld.dao.cinema.vue

import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import stub.Stubs

class VueCinemaDaoTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  test("retrieveCinemas") {
    withVueCinemaDao{ dao =>
      dao.retrieveCinemas().futureValue should not be empty
    }
  }

  test("retrieveMoviesAndPerformances for 1010882") {
    withVueCinemaDao{ dao =>
      val listings = dao.retrieveMoviesAndPerformances("10032", "2017-05-23").futureValue
      listings should not be empty
    }
  }

  def withVueCinemaDao[T](f: VueCinemaDao => T): T = {
    Stubs.withStubbedVue { repo =>
      val dao = new VueCinemaDao(repo)
      f(dao)
    }
  }
}
