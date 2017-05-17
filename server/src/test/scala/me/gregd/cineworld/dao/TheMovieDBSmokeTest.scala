package me.gregd.cineworld.dao

import fakes.{FakeCineworldRepository, FakeTheMovieDB}
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient

class TheMovieDBSmokeTest extends FunSuite with Matchers with OneAppPerSuite with ScalaFutures with IntegrationPatience {

  override lazy val app = new GuiceApplicationBuilder().overrides(bind[TheMovieDB].toInstance(FakeTheMovieDB), bind[CineworldRepository].toInstance(FakeCineworldRepository)).build

  val ws = app.injector.instanceOf[WSClient]
  val apiKey = app.configuration.getString("themoviedb.api-key").get
  val tmdb = new TheMovieDB(apiKey, ws)

  test("fetch imdb id") {
    tmdb.fetchImdbId("419430").futureValue shouldBe Some("tt5052448")
    tmdb.fetchImdbId("9999999999").futureValue shouldBe None
  }

  test("Fetch alternate title") {
    val size1 = tmdb.alternateTitles("550").futureValue.size
    val size2 = tmdb.alternateTitles("419430").futureValue.size

    size1 shouldBe 7
    size2 shouldBe 3
  }

  test("baseImageUrl") {
    tmdb.baseImageUrl should include ("image.tmdb.org")
  }

  test("Fetch now playing") {
    val actual = tmdb.fetchNowPlaying().futureValue
    actual.size should be > 10
  }

}
