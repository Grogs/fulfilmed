package me.gregd.cineworld.dao.cineworld

import com.google.inject.Guice
import me.gregd.cineworld.Config
import org.scalatest.{Matchers, FeatureSpec}

/**
 * Author: Greg Dorrell
 * Date: 03/09/2013
 */
class CineworldFeatureSpec extends FeatureSpec with Matchers {

  info("I should be able to get a list of films showing at my local cinema")

  val cineworld = Guice.createInjector(Config).getInstance(classOf[Cineworld])

  feature("Cineworld DAO") {
    scenario("Get list of cinemas:") {
      val cinemas = cineworld.retrieveCinemas
      assert(cinemas.size > 0 )
      assert(cinemas.find(
        _.name contains "West India Quay"
      ).isDefined)
    }
    scenario("Get listings for my local cinema:") {
      val localCinema = cineworld.retrieveCinemas.find(
        _.name contains "West India Quay"
      ).get
      val films = cineworld.retrieveMovies(localCinema.id)
      films should not be (null)
      films.size should be > (0)
    }
    scenario("Get show times for today") {
      val localCinema = cineworld.retrieveCinemas.find(
        _.name contains "West India Quay"
      ).get.id
      val performances = cineworld.retrievePerformances(localCinema)
      performances should not be (null)
      performances.size should be > (0)
      performances.find { film =>
        val performances = film._2
        performances.isDefined & performances.get.size > 0
      } should be ('defined)
    }
  }

}
