package me.gregd.cineworld.dao

import me.gregd.cineworld.Config
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.ws.WSClient

class TheMovieDBSmokeTest extends FunSuite with Matchers with OneAppPerSuite with ScalaFutures with IntegrationPatience {

  val ws = app.injector.instanceOf[WSClient]
  val tmdb = new TheMovieDB(Config.tmdbApiKey, ws)

  test("Fetch now playing") {
    val actual = tmdb.fetchNowPlaying().futureValue
    actual.size should be > 10
  }

  test("baseImageUrl") {
    tmdb.baseImageUrl should include ("image.tmdb.org")
  }

}
