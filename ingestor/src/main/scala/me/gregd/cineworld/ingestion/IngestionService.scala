package me.gregd.cineworld.ingestion

import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Cinema, Movie, Performance}
import me.gregd.cineworld.domain.repository.{CinemaRepository, ListingsRepository}
import me.gregd.cineworld.domain.service.{CinemasService, CompositeListingService, MovieService}
import me.gregd.cineworld.util.TaskRateLimiter
import monix.eval.Task

import scala.concurrent.duration._
import scala.util.control.NonFatal

class IngestionService(cinemaService: CinemasService,
                       listingsService: CompositeListingService,
                       listingsRepository: ListingsRepository[Task],
                       cinemaRepository: CinemaRepository[Task],
                       movieService: MovieService)
    extends LazyLogging {

  case class Listings(cinemaId: String, date: LocalDate, listings: Seq[(Movie, Seq[Performance])])

  def refresh(dates: Seq[LocalDate]): Task[Unit] = {
    for {
      _ <- Task.deferFuture(movieService.refresh())
      cinemas <- cinemaService.getCinemas()
      _ = logger.info(s"Retrieved ${cinemas.size} cinemas")
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

  private def fetchListings(permutations: Seq[(String, LocalDate)]): Task[Seq[Listings]] = {
    Task.wanderUnordered(permutations) {
      case (id, date) =>
        fetchListings(id, date)
    }
  }

  private def persistListings(allListings: Seq[Listings]) = {
    logger.info(s"Persisting ${allListings.size} listings")
    Task.wanderUnordered(allListings) { listings =>
      listingsRepository.persist(listings.cinemaId, listings.date)(listings.listings)
    }
  }

  val rateLimiter = TaskRateLimiter(2.seconds, 10)

  private def fetchListings(id: String, date: LocalDate) = {
    rateLimiter {
      listingsService.getMoviesAndPerformances(id, date).map(Listings(id, date, _))
    }.onErrorRecover {
      case NonFatal(e) =>
        logger.error(s"failed to retrieve listings for cinema $id")
        Listings(id, date, Nil)
    }
  }

}
