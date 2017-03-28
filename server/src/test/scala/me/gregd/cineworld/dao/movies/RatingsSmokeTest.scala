package me.gregd.cineworld.dao.movies

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.ws.WSClient

class RatingsSmokeTest extends FunSuite with OneAppPerSuite with Matchers with ScalaFutures with IntegrationPatience {

  val ws = app.injector.instanceOf[WSClient]
  val ratings = new Ratings(ws)

  test("get known rating") {
    val Some((rating, votes)) = ratings.imdbRatingAndVotes("tt0111161")

    rating should be >= 9.3
    votes should be >= 1700000
  }

  test("get known rating 2") {
    val (rating, votes) = ratings.ratingAndVotes("tt0111161").futureValue

    rating should be >= 9.3
    votes should be >= 1700000
  }

  test("invalid ID") {
    ratings.imdbRatingAndVotes("tt1337") shouldBe None
    ratings.imdbRatingAndVotes("tt3911337") shouldBe None
  }

}
