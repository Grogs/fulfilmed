package me.gregd.cineworld.dao

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import me.gregd.cineworld.Config

/**
 * Created by Greg Dorrell on 25/05/2014.
 */
class TheMovieDBTest extends FunSuite with ShouldMatchers {

  test("Initialisation") {
    new TheMovieDB(Config.tmdbApiKey)
  }

}
