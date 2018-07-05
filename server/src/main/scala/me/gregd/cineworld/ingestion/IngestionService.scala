package me.gregd.cineworld.ingestion

import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Cinema, Movie, Performance}
import me.gregd.cineworld.domain.repository.{CinemaRepository, ListingsRepository}
import me.gregd.cineworld.domain.service.{CompositeCinemaService, CompositeListingService, MovieService}
import me.gregd.cineworld.util.RateLimiter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.control.NonFatal

class IngestionService(cinemaService: CompositeCinemaService,
                       listingsService: CompositeListingService,
                       listingsRepository: ListingsRepository,
                       cinemaRepository: CinemaRepository,
                       movieService: MovieService)
    extends LazyLogging {

  case class Listings(cinemaId: String, date: LocalDate, listings: Map[Movie, Seq[Performance]])

  def refresh(dates: Seq[LocalDate]): Future[Unit] = {
    for {
      _ <- movieService.refresh()
      cinemas <- cinemaService.getCinemas()
      _ <- cinemaRepository.persist(cinemas)
      permutations = combinations(cinemas, dates)
      allListings <- fetchListings(permutations)
      _ <- persistListings(allListings)
    } yield ()
  }

  private def combinations(cinemas: Seq[Cinema], dates: Seq[LocalDate]) = {
    for {
      cinema <- cinemas
      date <- dates
    } yield cinema.id -> date
  }

  private def fetchListings(permutations: Seq[(String, LocalDate)]): Future[Seq[Listings]] = {
    Future.traverse(permutations) {
      case (id, date) =>
        fetchListings(id, date)
    }
  }

  private def persistListings(allListings: Seq[Listings]) = {
    Future.traverse(allListings) { listings =>
      listingsRepository.persist(listings.cinemaId, listings.date)(listings.listings)
    }
  }

  val rateLimiter = RateLimiter(2.seconds, 10)

  private def fetchListings(id: String, date: LocalDate) = {
    rateLimiter {
      listingsService.getMoviesAndPerformances(id, date).map(Listings(id, date, _))
    }.recover {
      case NonFatal(e) =>
        logger.error(s"failed to retrieve listings for cinema $id")
        Listings(id, date, Map.empty)
    }
  }

}
