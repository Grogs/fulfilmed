package me.gregd.cineworld.dao

import org.scalatest.{Matchers, FunSuite}
import me.gregd.cineworld.Config

/**
 * Created by Greg Dorrell on 25/05/2014.
 */
class TheMovieDBTest extends FunSuite with Matchers {

  test("Initialisation") {
    new TheMovieDB(Config.tmdbApiKey)
  }

  test("Get poster for tt2381991 (The Huntsmen sequel)") {
    new TheMovieDB(Config.tmdbApiKey).posterUrl("2381991")
  }

}
