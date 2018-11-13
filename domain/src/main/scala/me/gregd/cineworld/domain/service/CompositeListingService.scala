package me.gregd.cineworld.domain.service

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.util.Clock
import monix.eval.Task

class CompositeListingService(movieDao: MovieService, cineworld: CineworldService, vue: VueService, clock: Clock) {

  def getMoviesAndPerformances(cinemaId: String, date: LocalDate): Task[Seq[(Movie, Seq[Performance])]] = {
    //Relying on IDs not conflicting
    val cineworldResults = Task.deferFuture(
      cineworld.retrieveMoviesAndPerformances(cinemaId, date)
    )

    val vueResults = Task.deferFuture(
      vue.retrieveMoviesAndPerformances(cinemaId, date)
    )

    (cineworldResults onErrorFallbackTo vueResults).flatMap( res =>
      Task.traverse(res){ case (film, performances) =>
        Task.deferFuture(
          movieDao.toMovie(film)
        ).map(_ -> performances)
      }.map(_.toSeq)
    )
  }

}
