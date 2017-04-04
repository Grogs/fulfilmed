package fakes

import me.gregd.cineworld.dao.movies.{Ratings, RatingsCache}

import scala.concurrent.Future

object FakeRatings extends Ratings(null, new RatingsCache(collection.mutable.Map(), ())) {
  override def ratingAndVotes(id: String): Future[Option[(Double, Int)]] = Future.successful(None)
}
