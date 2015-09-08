package me.gregd.cineworld.dao.movies

import org.scalatest.{Matchers, FeatureSpec}

/**
 * Author: Greg Dorrell
 * Date: 11/09/2013
 */
class IMDbFeatureSpec extends FeatureSpec with Matchers {

  info("I should be able to to get ratings for films")

    feature("IMDB ID lookup") {
      scenario("Lookup id for The Dark Knight") {
        val id = Movies.getId("The Dark Knight")
        id should be (Some("tt0468569"))
      }
    }

//    feature("Rotten tomatoes ratings lookup") {
//      scenario("I should be able to retrieve the audience and critic rating from rotten tomatoes") {
//        val audienceRating = Movies.getAudienceRating("The Dark Knight")
//        val criticRating = Movies.getCriticRating("The Dark Knight")
//        criticRating should be ('defined)
//        criticRating.get should be > 50
//        audienceRating should be ('defined)
//        audienceRating.get should be > 50
//      }
//    }

    feature("IMDB rating and votes lookup") {
      scenario("Should be able to get the IMDB rating and votes for TDK") {
        val rating = Movies.getIMDbRating("tt0468569")
        val votes = Movies.getVotes("tt0468569")
        rating should be ('defined)
        rating.get should be > 5.0
        votes should be ('defined)
        votes.get should be > 50000
      }
    }

}
