package me.gregd.cineworld.dao.movies

import fakes.{FakeCineworldRepository, FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cineworld.CineworldRepository
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient

import scala.concurrent.Future

class RatingsCachingTest extends FunSuite with OneAppPerSuite with Matchers with ScalaFutures with IntegrationPatience with BeforeAndAfterEach {

  override lazy val app = new GuiceApplicationBuilder().overrides(bind[Ratings].toInstance(FakeRatings), bind[TheMovieDB].toInstance(FakeTheMovieDB), bind[CineworldRepository].toInstance(FakeCineworldRepository)).build

  val ws: WSClient = app.injector.instanceOf[WSClient]
  val cache = new RatingsCache(collection.mutable.Map(), ()) {
    def clear() = this.values.clear()
  }
  val ratings = new Ratings(ws, cache) {
    var invocations = 0
    override protected def fetchFromRemote(id: String): Future[Option[(Double, Int)]] = {
      invocations += 1
      super.fetchFromRemote(id)
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    ratings.invocations = 0
    cache.clear()
  }

  test("successful values are cached") {
    ratings.invocations shouldBe 0

    val Some((rating, votes)) = ratings.ratingAndVotes("tt0111161").futureValue

    rating should be >= 9.3
    votes should be >= 1700000

    ratings.invocations shouldBe 1

    for (i <- 1 to 5)
      ratings.ratingAndVotes("tt0111161").futureValue

    ratings.invocations shouldBe 1
  }

  test("invalid entries are not cached") {
    ratings.invocations shouldBe 0
    ratings.ratingAndVotes("tt1337").futureValue  shouldBe None
    ratings.ratingAndVotes("tt3911337").futureValue  shouldBe None
    ratings.invocations shouldBe 2
    ratings.ratingAndVotes("tt1337").futureValue  shouldBe None
    ratings.ratingAndVotes("tt3911337").futureValue  shouldBe None
    ratings.invocations shouldBe 4
  }

}
