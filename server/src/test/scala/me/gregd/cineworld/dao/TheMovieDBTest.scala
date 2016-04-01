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

}
