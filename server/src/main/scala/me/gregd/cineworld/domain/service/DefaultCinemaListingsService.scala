package me.gregd.cineworld.domain.service

import java.time.LocalDate

import me.gregd.cineworld.domain.ListingsService
import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.util.Clock

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultCinemaListingsService(movieDao: MovieService, cineworld: CineworldService, vue: VueService, clock: Clock) extends ListingsService {
  override def getMoviesAndPerformancesFor(cinemaId: String, dateRaw: String): Future[Map[Movie, Seq[Performance]]] =
    getMoviesAndPerformances(cinemaId, parse(dateRaw))

  def getMoviesAndPerformances(cinemaId: String, date: LocalDate): Future[Map[Movie, Seq[Performance]]] = {
    //Relying on IDs not conflicting
    val cineworldResults = cineworld.retrieveMoviesAndPerformances(cinemaId, date)
    val vueResults = vue.retrieveMoviesAndPerformances(cinemaId, date)

    (cineworldResults fallbackTo vueResults).flatMap( res =>
      Future.traverse(res){ case (film, performances) =>
        movieDao.toMovie(film).map(_ -> performances)
      }.map(_.toMap)
    )
  }

  private def parse(s: String) = s match {
    case "today"    => clock.today()
    case "tomorrow" => clock.today() plusDays 1
    case other      => LocalDate.parse(other)
  }

}
