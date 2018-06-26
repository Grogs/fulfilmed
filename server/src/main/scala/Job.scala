import java.net.URI
import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import better.files._
import me.gregd.cineworld.CinemaService
import me.gregd.cineworld.config.Config
import me.gregd.cineworld.domain.{Cinema, Coordinates, Movie, Performance}
import me.gregd.cineworld.util.{NoOpCache, RateLimiter, RealClock}
import me.gregd.cineworld.wiring.AppWiring
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, blocking}

object Job extends App {

  private val actorSystem = ActorSystem()

  val wsClient = AhcWSClient()(ActorMaterializer()(actorSystem))

  val config = Config.load() match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  val wiring = new AppWiring(wsClient, NoOpCache.cache, RealClock, config)

  val cinemaService = wiring.cinemaService

  println("Refreshing movies cache")
  Await.result(wiring.movieDao.refresh(), 2.minutes)
  println("Refreshed movies cache")

  val listings = new ListingsService(cinemaService)

  val start = System.currentTimeMillis()

  val store = new Store()

  val date = LocalDate.now plusDays 1

  println("Retrieving listings")
  val res = listings
    .retrieve(date)
    .flatMap(eventualListings =>
      Future.traverse(eventualListings) {
        case (cinema, performances) =>
          store.publish(cinema, date)(performances)
    })

  Await.result(res, Inf)
  println("Retrieved listings")

  val end = System.currentTimeMillis()

  val jobDurationSeconds = (end - start).millis.toSeconds

  println(s"Job executed in $jobDurationSeconds seconds")
  actorSystem.terminate()
  wsClient.close()
}

class ListingsService(cinemaService: CinemaService) {
  val rateLimiter = RateLimiter(2.seconds, 10)

  def retrieve(date: LocalDate): Future[Seq[(Cinema, Map[Movie, Seq[Performance]])]] = {
    cinemaService
      .getCinemas()
      .flatMap(cinemas =>
        Future.traverse(cinemas)(c =>
          rateLimiter {
            cinemaService.getMoviesAndPerformances(c.id, date).map(c -> _)
        }))
  }
}

class Store() {
  val bucket = File.currentWorkingDirectory

  implicit val coordinatesFormat = Json.format[Coordinates]
  implicit val performanceFormat = Json.format[Performance]
  implicit val movieFormat = Json.format[Movie]

  def publish(cinema: Cinema, date: LocalDate)(listings: Map[Movie, Seq[Performance]]) = {
    val path = bucket / s"listings-${cinema.id}-$date.json"
    Future {
      val json = Json.toBytes(Json.toJson(listings))
      blocking {
        path.writeByteArray(json)
      }
    }
  }
}
