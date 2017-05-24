package fakes

import me.gregd.cineworld.dao.movies.{Ratings, RatingsCache}

import scala.concurrent.Future

object FakeRatings extends Ratings(null, NoOpCache.cache, null) {

  val entries = Map(
    "tt3315342" -> (8.5, 204588),
    "tt3731562" ->  (7.1, 59166)
  )

  override def ratingAndVotes(id: String): Future[Option[(Double, Int)]] = Future.successful(entries.get(id))
}
