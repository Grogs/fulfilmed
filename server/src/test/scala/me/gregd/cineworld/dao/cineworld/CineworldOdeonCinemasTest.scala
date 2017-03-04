package me.gregd.cineworld.dao.cineworld

import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneAppPerSuite

/**
 * Created by Greg Dorrell on 01/07/2014.
 */
class CineworldOdeonCinemasTest extends FunSuite with Matchers with OneAppPerSuite {
  val cineworld = app.injector.instanceOf[Cineworld]

//  test("getOdeonCinemas should be non-empty") {
//    cineworld.retrieveOdeonCinemas() should not be ('empty)
//  }

  test("getOdeonFilms should be non-empty") {
//    cineworld.retrieveOdeonFilms(7167.toString, new LocalDate) should not be ('empty)
  }

  test("getOdeonPerformances should be non-empty") {
//    cineworld.retrieveOdeonPerformances(7167.toString, new LocalDate) should not be ('empty)
  }

}
