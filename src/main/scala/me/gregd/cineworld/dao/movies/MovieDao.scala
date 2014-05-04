package me.gregd.cineworld.dao.movies

import me.gregd.cineworld.domain.Movie

trait MovieDao {
  def getId(title: String): Option[String]
  def getIMDbRating(id:String) : Option[Double]
  def getVotes(id:String) : Option[Int]

  def find(title: String): Option[Movie]

}
