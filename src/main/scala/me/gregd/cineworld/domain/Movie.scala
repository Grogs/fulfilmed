package me.gregd.cineworld.domain

import me.gregd.cineworld.dao.imdb.{IMDbRating, IMDb, IMDbDao}
import me.gregd.cineworld.dao.cineworld.Cineworld

/**
 * Author: Greg Dorrell
 * Date: 11/05/2013
 */
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

class Format {
  object Default extends Format
  object `2D` extends Format
  object `3D` extends Format
  def split(title:String) = {
    title.take(5) match {
      case "2D - " => ("2D",title.substring(5))
      case "3D - " => ("3D",title.substring(5))
      case _       => ("default",title)
    }
  }
}
object Format extends Format {}