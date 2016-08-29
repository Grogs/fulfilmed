package me.gregd.cineworld.dao.cineworld

import com.google.inject.Guice
import me.gregd.cineworld.Config
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.joda.time.LocalDate

/**
 * Created by Greg Dorrell on 01/07/2014.
 */
class CineworldOdeonCinemasTest extends FunSuite with Matchers {
//  val cineworld = Guice.createInjector(Config).getInstance(classOf[Cineworld])

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
