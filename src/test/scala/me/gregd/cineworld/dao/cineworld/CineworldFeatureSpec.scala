package me.gregd.cineworld.dao.cineworld

import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.scalatest.matchers.ShouldMatchers

/**
 * Author: Greg Dorrell
 * Date: 03/09/2013
 */
class CineworldFeatureSpec extends FeatureSpec with ShouldMatchers {

  info("I should be able to get a list of films showing at my local cinema")

  feature("Cineworld DAO") {
    scenario("Get list of cinemas:") {
      val cinemas = Cineworld.getCinemas
      assert(cinemas.size > 0 )
      assert(cinemas.find(
        _.name contains "West India Quay"
      ).isDefined)
    }
    scenario("Get listings for my local cinema:") {
      val localCinema = Cineworld.getCinemas.find(
        _.name contains "West India Quay"
      ).get
      val films = Cineworld.getMovies(localCinema.id)
      films should not be (null)
      films.size should be > (0)
    }
  }

}
