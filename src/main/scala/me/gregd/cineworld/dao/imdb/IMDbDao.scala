package me.gregd.cineworld.dao.imdb

import me.gregd.cineworld.domain.Movie

/**
 * Author: Greg Dorrell
 * Date: 11/05/2013
 */
trait IMDbDao {
  def getId(title: String): Option[String]
  def getIMDbRating(id:String) : Option[Double]
  def getAudienceRating(id:String) : Option[Int]
  def getCriticRating(id:String) : Option[Int]
  def getVotes(id:String) : Option[Int]
}
