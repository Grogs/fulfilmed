package me.gregd.cineworld.dao.movies

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite

class RatingsSmokeTest extends FunSuite with OneAppPerSuite with Matchers with ScalaFutures with IntegrationPatience {

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
