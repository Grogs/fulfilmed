package me.gregd.cineworld.ingestion

import cats.effect.IO
import cats.implicits.{catsSyntaxParallelTraverse1, toTraverseOps}
import cats.syntax.flatMap._
import me.gregd.cineworld.domain.model.{Cinema, Listings, Movie, Performance}
import me.gregd.cineworld.domain.repository.{CinemaRepository, ListingsRepository}
import me.gregd.cineworld.domain.service.{CinemasService, CompositeListingService, MovieService}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import upperbound.Limiter
import upperbound.syntax.rate._

import java.time.LocalDate
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.control.NonFatal

class IngestionService(cinemaService: CinemasService,
                       listingsService: CompositeListingService,
                       listingsRepository: ListingsRepository,
                       cinemaRepository: CinemaRepository,
                       movieService: MovieService) {

  private val logger = Slf4jLogger.getLogger[IO]

  private val rateLimiter = Limiter.start[IO](10 every 2.seconds).allocated.unsafeRunSync()(cats.effect.unsafe.IORuntime.global)._1 //todo

  def refresh(dates: List[LocalDate]): IO[Unit] = {
    for {
      _           <- movieService.refresh
      cinemas     <- cinemaService.getCinemas
      _           <- logger.info(s"Retrieved ${cinemas.size} cinemas")
      _           <- cinemaRepository.persist(cinemas)
      allListings <- combinations(cinemas, dates).parTraverse { case (a, b) => fetchListings(a, b) }
      _           <- persistListings(allListings)
    } yield ()
  }

  private def combinations(cinemas: List[Cinema], dates: List[LocalDate]) = {
    for {
      cinema <- cinemas
      date   <- dates
    } yield cinema.id -> date
  }

  private def persistListings(allListings: List[Listings]) = {
    logger.info(s"Persisting ${allListings.size} listings") >>
      allListings.traverse { listings =>
        listingsRepository.persist(listings.cinemaId, listings.date)(
          listings.listings
        )
      }
  }

  private def fetchListings(id: String, date: LocalDate) = {
    rateLimiter
      .submit(
        listingsService.getMoviesAndPerformances(id, date)
      )
      .onError {
        case NonFatal(e) =>
          logger.error(s"failed to retrieve listings for cinema $id")
      }
  }

}
