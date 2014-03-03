package me.gregd.cineworld.domain

import me.gregd.cineworld.dao.imdb.{IMDbRating, Ratings, IMDbDao}
import me.gregd.cineworld.dao.cineworld.Cineworld

case class Movie (
  title: String,
  cineworldId: String,
  format: String,
  imdbId: Option[String],
  rating: Option[Double],
  votes: Option[Int],
  audienceRating: Option[Int],
  criticRating: Option[Int]
)

object Format {
  def split(title:String) = {
    title.take(5) match {
      case "2D - " | "(2D) " => ("2D",title.substring(5))
      case "3D - " => ("3D",title.substring(5))
      case _       => ("default",title)
    }
  }
}