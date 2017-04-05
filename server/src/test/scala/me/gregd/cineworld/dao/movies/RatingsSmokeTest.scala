package me.gregd.cineworld.dao.movies

import fakes.{FakeCineworldRepository, FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cineworld.CineworldRepository
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

class RatingsSmokeTest extends FunSuite with OneAppPerSuite with Matchers with ScalaFutures with IntegrationPatience {

  override lazy val app = new GuiceApplicationBuilder().overrides(bind[TheMovieDB].toInstance(FakeTheMovieDB), bind[CineworldRepository].toInstance(FakeCineworldRepository)).build

  val ratings = app.injector.instanceOf[Ratings]

  test("get known rating") {
    val Some((rating, votes)) = ratings.ratingAndVotes("tt0111161").futureValue

    rating should be >= 9.3
    votes should be >= 1700000
  }

  test("invalid ID") {
    ratings.ratingAndVotes("tt1337").futureValue shouldBe None
    ratings.ratingAndVotes("tt3911337").futureValue shouldBe None
  }

}
