package fakes

import me.gregd.cineworld.dao.movies.Ratings

import scala.concurrent.Future

object FakeRatings extends Ratings(null) {
  override def ratingAndVotes(id: String): Future[(Double, Int)] = Future.failed(new NotImplementedError())

  override def imdbRatingAndVotes(id: String): Option[(Double, Int)] = None
}
