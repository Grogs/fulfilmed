package me.gregd.cineworld.dao

import me.gregd.cineworld.Config
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.ws.WSClient

class TheMovieDBTest extends FunSuite with Matchers with OneAppPerSuite with ScalaFutures with IntegrationPatience {

  val ws = app.injector.instanceOf[WSClient]
  def tmdb = new TheMovieDB(Config.tmdbApiKey, ws)

  test("Initialisation") {
    tmdb
  }

  test("Fetch now playing") {
    val actual = tmdb.fetchNowPlaying().futureValue
    actual should not be 'empty
  }

}
