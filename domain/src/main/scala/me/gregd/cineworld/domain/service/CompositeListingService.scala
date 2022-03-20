package me.gregd.cineworld.domain.service

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeError

import java.time.LocalDate
import me.gregd.cineworld.domain.model.{Listings, Movie, MovieListing, Performance}
import me.gregd.cineworld.util.Clock
import cats.syntax.monadError._
import cats.syntax.traverse._

class CompositeListingService(movieDao: MovieService, cineworld: CineworldService, vue: VueService, clock: Clock) {

  def getMoviesAndPerformances(cinemaId: String, date: LocalDate): IO[Listings] = {
    //Relying on IDs not conflicting
    val cineworldResults = cineworld.retrieveMoviesAndPerformances(cinemaId, date)
    val vueResults       = vue.retrieveMoviesAndPerformances(cinemaId, date)
    import cats.implicits._
    (cineworldResults orElse vueResults)
      .flatMap(
        _.toList.traverse {
          case (film, performances) => movieDao.toMovie(film).map(MovieListing(_, performances))
        }
      ).map(Listings(cinemaId, date, _))
  }

}
