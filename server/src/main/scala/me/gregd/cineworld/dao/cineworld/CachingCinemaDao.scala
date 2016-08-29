package me.gregd.cineworld.dao.cineworld

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import grizzled.slf4j.Logging
import me.gregd.cineworld.domain._
import me.gregd.cineworld.util.OverridablePromise
import org.joda.time.LocalDate

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

@Singleton
class CachingCinemaDao @Inject()(remoteCineworld: RemoteCinemaDao, actorSystem: ActorSystem) extends CinemaDao with Logging {

  private var cinemas = OverridablePromise[ List[Cinema] ]()
  private var performances = OverridablePromise[ Map[(String, LocalDate), Map[String, Option[Seq[Performance]]]] ]()
  private var movies = OverridablePromise[ Map[(String, LocalDate), List[Movie]] ]()

  def run(): Unit = {
    refresh()
    val someMinutes = (8.hours.toMinutes + Random.nextInt(2.hours.toMinutes.toInt)).minutes
    actorSystem.scheduler.scheduleOnce(someMinutes)(
      run()
    )
  }

  def refresh() = {

    val eventualCinemas = remoteCineworld.retrieveCinemas()

    val todayTomorrow = (for {
      cinemas <- eventualCinemas
    } yield for {
      cinema <- cinemas
      day <- List(LocalDate.now(), LocalDate.now().plusDays(1))
      movies = remoteCineworld.retrieveMovies(cinema.id, day)
      performances = remoteCineworld.retrievePerformances(cinema.id, day)
    } yield for {
      ms <- movies
      ps <- performances
    } yield ((cinema, day), (ms, ps)))
      .map(Future.sequence(_)).flatMap(identity).map(_.toMap)

    todayTomorrow.onComplete {
      case Success(allTheData) =>
        this.cinemas.completeWith(allTheData.keys.map(_._1).toList)
        this.performances.completeWith(allTheData.map {
          case ((cinema, date), (_, performances)) =>
            (cinema.id, date) -> performances
        })
        this.movies.completeWith(allTheData.map {
          case ((cinema, date), (movies, _)) =>
            (cinema.id, date) -> movies
        })
      case Failure(ex) =>
        logger.error("Unable to refresh caching cinema dao")
    }

  }

  def retrieveCinemas() = cinemas.future

  def retrievePerformances(cinema: String, date: LocalDate) = performances.future.map(_(cinema, date))

  def retrieveMovies(cinema: String, date: LocalDate) = movies.future.map(_(cinema, date))

}
