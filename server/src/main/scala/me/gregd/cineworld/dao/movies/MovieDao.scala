package me.gregd.cineworld.dao.movies

import com.google.inject.ImplementedBy
import me.gregd.cineworld.domain.{Film, Movie}

@ImplementedBy(classOf[Movies])
trait MovieDao {
  def getId(title: String): Option[String]
  def getIMDbRating(id:String) : Option[Double]
  def getVotes(id:String) : Option[Int]

  def toMovie(film: Film): Movie

  def find(title: String): Option[Movie]

}
