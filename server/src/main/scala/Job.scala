import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import better.files._
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}
import me.gregd.cineworld.util.RateLimiter
import me.gregd.cineworld.wiring.ProdAppWiring
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Job extends App {
  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val wiring = new ProdAppWiring(wsClient)

  val cinemaService = wiring.cinemaService

  val bucket = File("gs://fulfilmed-listings")

  val rateLimiter = RateLimiter(2.seconds, 10)

  val start = System.currentTimeMillis()

//  TODO make sure movies cache is warm first

  val allListings: Future[List[(Cinema, Map[Movie, List[Performance]])]] =
    cinemaService.getCinemas().flatMap(Future.traverse(_)(c =>
      rateLimiter{
        cinemaService.getMoviesAndPerformances(c.id, "today").map(c -> _)
      }
    ))

  allListings.map { all =>
    val end = System.currentTimeMillis()
    val duration = (end - start).millis.toSeconds
    println("=======")
    println(all)
    println("=======")
    val res = for {
        (cinema, listings) <- all
    } yield bucket / s"listings-$cinema-"
//TODO write to bucket :D
    println(res)
    println("=======")
    println(s"Took $duration seconds")

  }

//  def render
}
