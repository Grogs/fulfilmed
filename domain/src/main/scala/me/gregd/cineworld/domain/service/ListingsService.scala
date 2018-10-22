package me.gregd.cineworld.domain.service

import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.domain.repository.ListingsRepository
import me.gregd.cineworld.util.Clock

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ListingsService(listingsRepository: ListingsRepository, clock: Clock) extends Listings with LazyLogging {
  def getMoviesAndPerformancesFor(cinemaId: String, dateRaw: String): Future[Seq[(Movie, Seq[Performance])]] = {
    listingsRepository.fetch(cinemaId, parse(dateRaw)).recover{ case e =>
      logger.error("Failed to retrieve listings", e)
      Nil
    }
  }

  private def parse(s: String) = s match {
    case "today"    => clock.today()
    case "tomorrow" => clock.today() plusDays 1
    case other      => LocalDate.parse(other)
  }
}
