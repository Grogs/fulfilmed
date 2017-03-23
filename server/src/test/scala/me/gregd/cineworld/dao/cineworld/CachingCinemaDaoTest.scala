package me.gregd.cineworld.dao.cineworld

import fakes.{FakeCineworldRepository, FakeTheMovieDB}
import me.gregd.cineworld.dao.movies.Movies
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures

class CachingCinemaDaoTest extends FunSuite with ScalaFutures with Matchers {

  val fakeRemoteCinemaDao = new RemoteCinemaDao(new Movies(FakeTheMovieDB), FakeTheMovieDB, FakeCineworldRepository)
  val cachingCinemaDao = new CachingCinemaDao(fakeRemoteCinemaDao)

}
