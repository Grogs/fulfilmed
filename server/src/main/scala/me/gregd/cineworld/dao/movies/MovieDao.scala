package me.gregd.cineworld.dao.movies

import com.google.inject.ImplementedBy
import me.gregd.cineworld.domain.Movie

@ImplementedBy(classOf[Movies])
trait MovieDao {
  def getId(title: String): Option[String]
  def getIMDbRating(id:String) : Option[Double]
  def getVotes(id:String) : Option[Int]

  def find(title: String): Option[Movie]

}
