package me.gregd.cineworld.domain.service

import cats.effect.IO
import cats.implicits.toFunctorOps

import java.time.LocalDate
import cats.syntax.applicativeError._
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Listings, Movie, Performance}
import me.gregd.cineworld.domain.repository.ListingsRepository
import me.gregd.cineworld.util.Clock

class DefaultListingsService(listingsRepository: ListingsRepository, clock: Clock) extends ListingsService with LazyLogging {
  def getMoviesAndPerformancesFor(cinemaId: String, dateRaw: String): IO[Listings] = {
    val date = parse(dateRaw)
    listingsRepository
      .fetch(cinemaId, date)
      .handleError { e =>
        logger.error("Failed to retrieve listings", e)
        Nil
      }
      .map(Listings(cinemaId, date, _))
  }

  private def parse(s: String) = s match {
    case "today"    => clock.today()
    case "tomorrow" => clock.today() plusDays 1
    case other      => LocalDate.parse(other)
  }
}
