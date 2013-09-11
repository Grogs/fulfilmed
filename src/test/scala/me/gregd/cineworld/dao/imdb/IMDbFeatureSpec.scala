package me.gregd.cineworld.dao.imdb

import org.scalatest.FeatureSpec
import org.scalatest.matchers.ShouldMatchers

/**
 * Author: Greg Dorrell
 * Date: 11/09/2013
 */
class IMDbFeatureSpec extends FeatureSpec with ShouldMatchers {

  info("I should be able to to get ratings for films")

    feature("IMDB ID lookup") {
      scenario("Lookup id for The Dark Knight") {
        val id = IMDb.getId("The Dark Knight")
        id should be (Some("tt0468569"))
      }
    }

    feature("Rotten tomatoes ratings lookup") {
      scenario("I should be able to retrieve the audience and critic rating from rotten tomatoes") {
        val audienceRating = IMDb.getAudienceRating("The Dark Knight")
        val criticRating = IMDb.getCriticRating("The Dark Knight")
        criticRating should be ('defined)
        criticRating.get should be > 50
        audienceRating should be ('defined)
        audienceRating.get should be > 50
      }
    }

    feature("IMDB rating and votes lookup") {
      scenario("Should be able to get the IMDB rating and votes for TDK") {
        val rating = IMDb.getIMDbRating("tt0468569")
        val votes = IMDb.getVotes("tt0468569")
        rating should be ('defined)
        rating.get should be > 5.0
        votes should be ('defined)
        votes.get should be > 50000
      }
    }

}
