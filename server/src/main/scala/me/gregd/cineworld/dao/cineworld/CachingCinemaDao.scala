package me.gregd.cineworld.dao.cineworld

import java.time.LocalDate.now
import javax.inject.{Inject, Singleton}

import grizzled.slf4j.Logging
import me.gregd.cineworld.domain._
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

@Singleton
class CachingCinemaDao @Inject()(remoteCineworld: RemoteCinemaDao, scheduler: Scheduler) extends CinemaDao with Logging {

  type Listings = Map[(String, String), Map[Movie, List[Performance]]]

  private var cinemas: Future[Seq[Cinema]] = remoteCineworld.retrieveCinemas()
  private var listings: Future[Listings] = fetchListings(cinemas)

  def run(): Unit = {
    val someMinutes = (1.hour.toMinutes + Random.nextInt(1.hour.toMinutes.toInt)).seconds
    scheduler.scheduleOnce(someMinutes) {
      refresh()
      run()
    }
  }

  logger.info("Scheduling refresh")
  scheduler.scheduleOnce(5.seconds)(run())

  protected def fetchListings(eventualCinemas: Future[Seq[Cinema]]): Future[Listings] = {
    (
      for {
        cinemas <- eventualCinemas
      } yield
        for {
          cinema <- cinemas
          day <- (0 to 2).map( now plusDays _ toString )
          _ = logger.debug(s"Retrieving listings for ${cinema.id} / $day")
          listings = remoteCineworld.retrieveMoviesAndPerformances(cinema.id, day)
        } yield
          for {
            ls <- listings
          } yield ((cinema.id, day), ls)
    ).map(Future.sequence(_)).flatMap(identity).map(_.toMap)
  }

  protected def refreshCinemas() = {
    logger.info("Refreshing cinema")

    val next = for {
      nextCinemas <- remoteCineworld.retrieveCinemas()
      _ = logger.info("Retrieved cinemas")
    } yield nextCinemas

    next.onComplete {
      case Success(_) =>
        logger.info("Successfully refreshed cinemas")
        this.cinemas = next
      case Failure(ex) =>
        logger.error("Unable to refresh cinemas", ex)
    }

    next
  }

  protected def refreshListings(eventualCinemas: Future[Seq[Cinema]]) = {
    logger.info("Refreshing listings")

    val next = for {
      nextCinemas <- eventualCinemas
      nextListings <- fetchListings(cinemas)
      _ = logger.info("Fetched listings")
    } yield nextListings

    next.onComplete {
      case Success(_) =>
        logger.info("Successfully refreshed listings")
        this.listings = next
      case Failure(ex) =>
        logger.error("Unable to refresh listings", ex)
    }

    next
  }

  def refresh(): Future[Unit] = {
    refreshListings(refreshCinemas()).map(_ => ())
  }

  def retrieveCinemas() = cinemas orElse refreshCinemas()

  def retrieveMoviesAndPerformances(cinema: String, date: String) = {
    def forRequest(l: Listings) =
      l((cinema, date))
    listings.map(forRequest) orElse refreshListings(cinemas).map(forRequest)
  }

  implicit class FutureUtil[T](f1: Future[T]) {
    def orElse(f2: => Future[T]): Future[T] = {
      if (f1.isCompleted && f1.value.get.isFailure) {
        f2
      } else {
        f1 recoverWith { case _ => f2 }
      }
    }
  }

}
