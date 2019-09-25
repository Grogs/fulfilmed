package me.gregd.cineworld.domain.service

import java.time.LocalDate

import cats.syntax.applicativeError._
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.domain.repository.ListingsRepository
import me.gregd.cineworld.util.Clock


class ListingsService[F[_]: ApplicativeThrowable](listingsRepository: ListingsRepository[F], clock: Clock) extends Listings[F] with LazyLogging {
  def getMoviesAndPerformancesFor(cinemaId: String, dateRaw: String): F[Seq[(Movie, Seq[Performance])]] = {
    listingsRepository.fetch(cinemaId, parse(dateRaw)).handleError{ e  =>
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
