package me.gregd.cineworld.dao.ratings

import play.api.libs.json.Json

case class RatingsResult(
    imdbRating: Option[Double],
    imdbVotes: Option[Int],
    metascore: Option[Int],
    rottenTomatoes: Option[String]
)

object RatingsResult {
  implicit val jsonFormats = Json.format[RatingsResult]
}