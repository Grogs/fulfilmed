package me.gregd.cineworld.service

import me.gregd.cineworld.domain.thrift.{CinemaService => TCS, Movie}
import me.gregd.cineworld.dao.cineworld.Film
import com.twitter.util.Future
import me.gregd.cineworld.domain.{Movie=>InternalMovie}

class CinemaService extends TCS.FutureIface {
  override def getMovie(title: String): Future[Movie] = {
    Future({
      val m = Film("", title).toMovie
      val p = InternalMovie.unapply(m).get
      (Movie.apply _).tupled apply p
      Movie(m.title, m.cineworldId, m.format)
    })
  }
}