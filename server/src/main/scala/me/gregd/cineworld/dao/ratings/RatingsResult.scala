package me.gregd.cineworld.dao.ratings

case class RatingsResult(
    imdbRating: Option[Double],
    imdbVotes: Option[Int],
    metascore: Option[Int],
    rottenTomatoes: Option[String]
)
