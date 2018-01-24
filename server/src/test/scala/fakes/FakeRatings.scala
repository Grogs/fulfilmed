package fakes

import me.gregd.cineworld.config.OmdbConfig
import me.gregd.cineworld.dao.ratings.{Ratings, RatingsResult}

import scala.concurrent.Future
import eu.timepit.refined.auto._
import me.gregd.cineworld.util.NoOpCache

object FakeRatings extends Ratings(null, NoOpCache.cache, OmdbConfig("http://dummy","")) {

  val someRatingAndVotes = RatingsResult(Some(6.9), Some(1337), None, None)

  val entries = Map(
    "tt3315342" -> RatingsResult(Some(8.5), Some(204588), None, None),
    "tt3731562" -> RatingsResult(Some(7.1), Some(59166), None, None),
    "tt3315342" -> someRatingAndVotes,
    "tt3228088" -> someRatingAndVotes,
    "tt7777777" -> someRatingAndVotes
  ).withDefaultValue(RatingsResult(None, None, None, None))

  override def fetchRatings(imdbId: String): Future[RatingsResult] = {
    Future.successful(entries(imdbId))
  }
}
