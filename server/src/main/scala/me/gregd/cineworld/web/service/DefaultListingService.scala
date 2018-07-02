package me.gregd.cineworld.web.service

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.domain.service.CompositeListingService
import me.gregd.cineworld.util.Clock

import scala.concurrent.Future

class DefaultListingService(compositeListingService: CompositeListingService, clock: Clock) extends ListingsService {
  def getMoviesAndPerformancesFor(cinemaId: String, dateRaw: String): Future[Map[Movie, Seq[Performance]]] =
    compositeListingService.getMoviesAndPerformances(cinemaId, parse(dateRaw))

  private def parse(s: String) = s match {
    case "today"    => clock.today()
    case "tomorrow" => clock.today() plusDays 1
    case other      => LocalDate.parse(other)
  }

}
