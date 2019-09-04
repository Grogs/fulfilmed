package me.gregd.cineworld.integration.cineworld

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.FakeOmdbService
import me.gregd.cineworld.domain._
import me.gregd.cineworld.domain.model._
import me.gregd.cineworld.domain.service.{CineworldService, MovieService}
import me.gregd.cineworld.integration.PostcodeIoIntegrationService
import me.gregd.cineworld.integration.cineworld.CineworldIntegrationService
import me.gregd.cineworld.integration.tmdb.TmdbIntegrationService
import me.gregd.cineworld.util.{NoOpCache, RealClock}
import me.gregd.cineworld.config.MoviesConfig
import monix.execution.Scheduler
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import util.WSClient

import scala.concurrent.duration._

class CineworldServiceTest extends FunSuite with ScalaFutures with Matchers with Eventually with WSClient {

  implicit val defaultPatienceConfig = PatienceConfig(Span(3000, Millis))

  val tmdb = new TmdbIntegrationService(wsClient, NoOpCache.cache, Scheduler.global, Stubs.tmdb.config)
  val movieDao = new MovieService(tmdb, FakeOmdbService, MoviesConfig(1.second))
  val cineworldRaw = new CineworldIntegrationService(wsClient, NoOpCache.cache, Stubs.cineworld.config, RealClock)
  val postcodeService = new PostcodeIoIntegrationService(Stubs.postcodesio.config, wsClient)
  val cineworld = new CineworldService(cineworldRaw, postcodeService)

  test("retrieveCinemas") {
    val cinemas = cineworld.retrieveCinemas().futureValue.take(3)
    cinemas shouldEqual expectedCinemas
  }

  test("retrieveMoviesAndPerformances") {
    val date = LocalDate.parse("2017-05-23")
    val showings = cineworld.retrieveMoviesAndPerformances("8112", date).futureValue
//    pprint.pprintln(showings, height = 100000, width = 200)
    showings should contain allElementsOf expectedShowings
  }

  val expectedCinemas = List(
    Cinema("8014", "Cineworld", "Aberdeen - Queens Links", Option(Coordinates(57.1502699571208, -2.07796067079163))),
    Cinema("8018", "Cineworld", "Aberdeen - Union Square", Option(Coordinates(57.1443735096293, -2.09607620679942))),
    Cinema("8015", "Cineworld", "Aldershot", Option(Coordinates(51.2496276978637, -0.76918738639163)))
  )

  val expectedShowings: Map[Film, List[Performance]] = Map(
    Film("ho00005039", "Duck Duck Goose", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005039-md.jpg") -> List(
      Performance("10:40", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46903", Some("2018-04-12")),
      Performance("13:10", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46904", Some("2018-04-12")),
      Performance("15:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46905", Some("2018-04-12"))
    ),
    Film("ho00005037", "Ready Player One", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005037-md.jpg") -> List(
      Performance("10:45", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46924", Some("2018-04-12")),
      Performance("14:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46923", Some("2018-04-12")),
      Performance("17:10", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46929", Some("2018-04-12")),
      Performance("19:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46932", Some("2018-04-12")),
      Performance("20:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46925", Some("2018-04-12"))
    ),
    Film("ho00005022", "Thoroughbreds", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005022-md.jpg") -> List(
      Performance("16:45", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46888", Some("2018-04-12")),
      Performance("19:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46889", Some("2018-04-12"))
    ),
    Film("ho00004610", "The Boss Baby - Movies For Juniors", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004610-md.jpg") -> List(
      Performance("10:10", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46917", Some("2018-04-12"))
    ),
    Film("ho00005063", "A Quiet Place", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005063-md.jpg") -> List(
      Performance("11:45", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46881", Some("2018-04-12")),
      Performance("14:10", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46882", Some("2018-04-12")),
      Performance("16:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46883", Some("2018-04-12")),
      Performance("18:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46884", Some("2018-04-12")),
      Performance("20:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46875", Some("2018-04-12")),
      Performance("21:15", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46885", Some("2018-04-12"))
    ),
    Film("ho00004787", "The Greatest Showman", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004787-md.jpg") -> List(
      Performance("12:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46926", Some("2018-04-12")),
      Performance("15:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46927", Some("2018-04-12")),
      Performance("17:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46928", Some("2018-04-12")),
      Performance("20:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46916", Some("2018-04-12"))
    ),
    Film("ho00005044", "Rampage", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005044-md.jpg") -> List(
      Performance("10:00", available = true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46918", Some("2018-04-12")),
      Performance("11:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46933", Some("2018-04-12")),
      Performance("12:30", available = true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46919", Some("2018-04-12")),
      Performance("14:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46934", Some("2018-04-12")),
      Performance("15:00", available = true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46920", Some("2018-04-12")),
      Performance("17:15", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46935", Some("2018-04-12")),
      Performance("17:35", available = true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46921", Some("2018-04-12")),
      Performance("19:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46936", Some("2018-04-12")),
      Performance("20:20", available = true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46922", Some("2018-04-12")),
      Performance("21:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46931", Some("2018-04-12"))
    ),
    Film("ho00005028", "Peter Rabbit", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005028-md.jpg") -> List(
      Performance("10:10", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46871", Some("2018-04-12")),
      Performance("10:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46876", Some("2018-04-12")),
      Performance("11:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46886", Some("2018-04-12")),
      Performance("12:40", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46872", Some("2018-04-12")),
      Performance("13:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46877", Some("2018-04-12")),
      Performance("14:15", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46887", Some("2018-04-12")),
      Performance("15:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46873", Some("2018-04-12")),
      Performance("15:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46878", Some("2018-04-12")),
      Performance("17:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46874", Some("2018-04-12")),
      Performance("18:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46879", Some("2018-04-12"))
    ),
    Film("ho00005049", "Blockers", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005049-md.jpg") -> List(
      Performance("15:40", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46900", Some("2018-04-12")),
      Performance("18:10", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46901", Some("2018-04-12")),
      Performance("20:40", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46902", Some("2018-04-12"))
    ),
    Film("ho00005032", "Pacific Rim Uprising", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005032-md.jpg") -> List(
      Performance("12:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46890", Some("2018-04-12")),
      Performance("15:10", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46891", Some("2018-04-12")),
      Performance("17:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46892", Some("2018-04-12")),
      Performance("20:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46941", Some("2018-04-12"))
    ),
    Film("ho00005287", "Pitbull: Ostatni Pies (Polish)", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005287-md.jpg") -> List(
      Performance("20:15", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=45700", Some("2018-04-12"))
    ),
    Film("ho00005313", "Early Man Movies For Juniors", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005313-md.jpg") -> List(
      Performance("10:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46930", Some("2018-04-12"))
    ),
    Film("ho00004790", "Black Panther", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004790-md.jpg") -> List(
      Performance("10:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46895", Some("2018-04-12")),
      Performance("13:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46896", Some("2018-04-12")),
      Performance("16:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46897", Some("2018-04-12")),
      Performance("19:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46915", Some("2018-04-12"))
    ),
    Film(
      "ho00005292",
      "Tad The Explorer And The Secret Of King Midas Movies For Juniors",
      "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005292-md.jpg"
    ) -> List(
      Performance("10:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46914", Some("2018-04-12"))
    ),
    Film("ho00005040", "Isle Of Dogs", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005040-md.jpg") -> List(
      Performance("12:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46913", Some("2018-04-12")),
      Performance("14:50", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46893", Some("2018-04-12")),
      Performance("17:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46894", Some("2018-04-12"))
    ),
    Film("ho00005030", "Love, Simon", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005030-md.jpg") -> List(
      Performance("11:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46863", Some("2018-04-12")),
      Performance("14:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46864", Some("2018-04-12")),
      Performance("17:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46865", Some("2018-04-12")),
      Performance("19:40", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46912", Some("2018-04-12"))
    ),
    Film("ho00004273", "Coco", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004273-md.jpg") -> List(
      Performance("10:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46866", Some("2018-04-12"))
    ),
    Film("ho00005261", "Death Wish", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005261-md.jpg") -> List(
      Performance("17:45", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46906", Some("2018-04-12")),
      Performance("20:20", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46907", Some("2018-04-12"))
    ),
    Film("ho00005035", "A Wrinkle In Time", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005035-md.jpg") -> List(
      Performance("10:15", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46898", Some("2018-04-12")),
      Performance("13:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46899", Some("2018-04-12"))
    ),
    Film("ho00004888", "Tomb Raider", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004888-md.jpg") -> List(
      Performance("10:45", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46908", Some("2018-04-12")),
      Performance("14:40", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46909", Some("2018-04-12")),
      Performance("17:40", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46910", Some("2018-04-12")),
      Performance("20:45", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46911", Some("2018-04-12"))
    ),
    Film("ho00004957", "Molly's Game", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004957-md.jpg") -> List(
      Performance("11:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46940", Some("2018-04-12"))
    ),
    Film("ho00005051", "Ghost Stories", "https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005051-md.jpg") -> List(
      Performance("13:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46867", Some("2018-04-12")),
      Performance("16:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46868", Some("2018-04-12")),
      Performance("18:30", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46869", Some("2018-04-12")),
      Performance("21:00", available = true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46870", Some("2018-04-12"))
    )
  )
}
