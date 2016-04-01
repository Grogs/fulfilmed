package me.gregd.cineworld.dao.movies

/**
 * Created by Greg Dorrell on 03/05/2014.
 */
case class RTMovie(
  title: String,
  year: Option[Int],
  ratings: RTRating,
  posters: RTPosters,
  alternate_ids: RTIds,
  links: RTLinks
)

case class RTRating(critics_score: Option[Int], audience_score: Option[Int])

case class RTIds(imdb: Option[String])

case class RTPosters(thumbnail: Option[String], profile: Option[String], detailed: Option[String], original: Option[String])

case class RTLinks(alternate: Option[String])