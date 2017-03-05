package me.gregd.cineworld.dao.cineworld

import java.lang.management.ManagementFactory
import javax.inject.{Inject, Singleton}
import javax.management.ObjectName

import akka.actor.ActorSystem
import grizzled.slf4j.Logging
import me.gregd.cineworld.domain._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success, Try}

@Singleton
class CachingCinemaDao @Inject()(remoteCineworld: RemoteCinemaDao, actorSystem: ActorSystem) extends CinemaDao with Logging {

  private var cinemas = Promise[Seq[Cinema]]
  private val listings = Promise[Map[(String, String), Map[Movie, List[Performance]]]]


  def run(): Unit = {
    refresh()
    val someMinutes = (1.hour.toMinutes + Random.nextInt(1.hour.toMinutes.toInt)).seconds
    actorSystem.scheduler.scheduleOnce(someMinutes)(
      run()
    )
  }

  logger.info("Scheduling refresh")
    actorSystem.scheduler.scheduleOnce(5.seconds)(run())

  def refresh() = {
    logger.info("Refreshing")
    val eventualCinemas = remoteCineworld.retrieveCinemas()

    val todayTomorrow = (for {
      cinemas <- eventualCinemas
      _ = logger.debug(s"Retrieved cinemas")
    } yield for {
      cinema <- cinemas
      day <- List("today", "tomorrow")
      _ = logger.debug(s"Retrieving listings for ${cinema.id} / $day")
      listings = remoteCineworld.retrieveMoviesAndPerformances(cinema.id, day)
    } yield for {
      ls <- listings
    } yield ((cinema.id, day), ls))
      .map(Future.sequence(_)).flatMap(identity).map(_.toMap)

    todayTomorrow.onComplete {
      case Success(allTheData) =>
        logger.info("Successfully refreshed")
        this.cinemas.completeWith(eventualCinemas)
        this.listings.success(allTheData)
      case Failure(ex) =>
        logger.error("Unable to refresh caching cinema dao", ex)
    }
  }

  def retrieveCinemas() = cinemas.future

  def retrieveMoviesAndPerformances(cinema: String, date: String) = {

    def fallback() = remoteCineworld.retrieveMoviesAndPerformances(cinema, date)

    logger.info(s"querying for $cinema:$date")

    Try(Await.result(listings.future, 2.seconds)) match {
      case Success(allListings) => allListings.get((cinema, date)) match {
        case Some(listings) =>
          logger.debug(s"Cache hit for $cinema:$date")
          Future.successful(listings)
        case None =>
          logger.debug(s"Cache miss for $cinema:$date")
          fallback()
      }
      case Failure(ex) =>
        logger.error(s"exception from cache for $cinema:$date", ex)
        fallback()
    }

  }

}
