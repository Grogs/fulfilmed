package me.gregd.cineworld.dao.cinema.vue

import javax.inject.Inject

import me.gregd.cineworld.dao.cinema.CinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VueCinemaDao @Inject() (vueRepository: VueRepository) extends CinemaDao {

  def retrieveCinemas(): Future[Seq[Cinema]] = {
    vueRepository.retrieveCinemas().map( raw =>
      for { c <- raw } yield Cinema(c.id, c.name)
    )
  }

  def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] = {
    vueRepository.retrieveListings(cinemaId).map { raw =>

      val converted = for {
        f <- raw.films
        movie = Movie(f.title, None, None, None, None, None, None, None, None, Option(f.image_poster)) //TODO
        performances = f.showings.map { s =>
          Performance(s.date_time, available = true, "", "", None) //TODO
        }
      } yield movie -> performances

      converted.toMap
    }
  }
}
