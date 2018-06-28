package me.gregd.cineworld.domain.movies

import com.google.inject.ImplementedBy
import me.gregd.cineworld.domain.model.{Film, Movie}

import scala.concurrent.Future

@ImplementedBy(classOf[Movies])
trait MovieDao {

  def toMovie(film: Film): Future[Movie]

}
