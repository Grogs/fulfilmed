package me.gregd.cineworld.dao.cinema.vue

import fakes.{FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.movies.Movies
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import stub.Stubs

class VueCinemaDaoTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  val movieDao = new Movies(FakeTheMovieDB, FakeRatings)

  test("retrieveCinemas") {
    withVueCinemaDao{ dao =>
      dao.retrieveCinemas().futureValue should not be empty
    }
  }

  test("retrieveMoviesAndPerformances for 1010882") {
    withVueCinemaDao{ dao =>
      val listings = dao.retrieveMoviesAndPerformances("10032", "2017-05-23").futureValue
      listings.take(3).foreach{ case (movie, performances) =>
        movie.title should not be empty
        movie.posterUrl should not be empty
        movie.imdbId should not be empty
      }
    }
  }

  def withVueCinemaDao[T](f: VueCinemaDao => T): T = {
    Stubs.withStubbedVue { repo =>
      val dao = new VueCinemaDao(repo, movieDao)
      f(dao)
    }
  }

}
