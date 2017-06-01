package me.gregd.cineworld.dao.cinema.vue

import javax.inject.Inject

import me.gregd.cineworld.dao.cinema.CinemaDao
import me.gregd.cineworld.dao.cinema.vue.raw.VueRepository
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Cinema, Film, Movie, Performance}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VueCinemaDao @Inject() (vueRepository: VueRepository, imdb: MovieDao) extends CinemaDao {

  def retrieveCinemas(): Future[Seq[Cinema]] = {
    vueRepository.retrieveCinemas().map( raw =>
      for { c <- raw } yield Cinema(c.id, c.name)
    )
  }

  def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String): Future[Map[Movie, List[Performance]]] = {
    vueRepository.retrieveListings(cinemaId).flatMap { raw =>

      val converted = for {
        f <- raw.films
        image = ImageUrl.resolve(f.image_poster)
        film = Film(f.id, f.title, image)
        movie = imdb.toMovie(film)
        performances = f.showings.map { s =>
          Performance(s.date_time, available = true, "", "", None) //TODO
        }
      } yield movie.map(_ -> performances)

      Future.sequence(converted).map(_.toMap)
    }
  }
}
