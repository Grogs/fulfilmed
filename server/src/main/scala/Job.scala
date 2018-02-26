import java.net.URI
import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import better.files._
import me.gregd.cineworld.CinemaService
import me.gregd.cineworld.domain.{Cinema, Coordinates, Movie, Performance}
import me.gregd.cineworld.util.RateLimiter
import me.gregd.cineworld.wiring.ProdAppWiring
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future, blocking}
import scala.concurrent.duration._

object Job extends App {

  private val actorSystem = ActorSystem()

  val wsClient = AhcWSClient()(ActorMaterializer()(actorSystem))
  val wiring = new ProdAppWiring(wsClient)

  val cinemaService = wiring.cinemaService
  //  TODO make sure movies cache is warm first
  val listings = new Listings(cinemaService)

  val start = System.currentTimeMillis()

  val store = new Store()

  val date = LocalDate.now plusDays 1

  val res = listings
    .retrieve(date)
    .flatMap(eventualListings =>
      Future.traverse(eventualListings) {
        case (cinema, performances) =>
          store.publish(cinema, date)(performances)
    })

  Await.result(res, Inf)


  val end = System.currentTimeMillis()

  val jobDurationSeconds = (end - start).millis.toSeconds

  println(s"Job executed in $jobDurationSeconds seconds")
  actorSystem.terminate()
  wsClient.close()
}

class Listings(cinemaService: CinemaService) {
  val rateLimiter = RateLimiter(2.seconds, 10)

  def retrieve(date: LocalDate): Future[List[(Cinema, Map[Movie, List[Performance]])]] = {
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
  val bucket = File(URI.create("gs://fulfilmed-listings"))

  implicit val coordinatesFormat = Json.format[Coordinates]
  implicit val performanceFormat = Json.format[Performance]
  implicit val movieFormat = Json.format[Movie]

  def publish(cinema: Cinema, date: LocalDate)(listings: Map[Movie, List[Performance]]) = {
    val path = bucket / s"listings-${cinema.id}-$date.json"
    Future {
      val json = Json.toBytes(Json.toJson(listings))
      blocking {
        path.writeByteArray(json)
      }
    }
  }
}
