package me.gregd.cineworld.dao.cineworld

import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.joda.time.LocalDate

/**
 * Created by Greg Dorrell on 01/07/2014.
 */
class CineworldOdeonCinemasTest extends FunSuite with Matchers {
  val cineworld = new Cineworld(null,null)

  test("getOdeonCinemas should be non-empty") {
    cineworld.getOdeonCinemas() should not be ('empty)
  }

  test("getOdeonFilms should be non-empty") {
    cineworld.getOdeonFilms(7167.toString, new LocalDate) should not be ('empty)
  }

  test("getOdeonPerformances should be non-empty") {
    cineworld.getOdeonPerformances(7167.toString, new LocalDate) should not be ('empty)
  }

}
