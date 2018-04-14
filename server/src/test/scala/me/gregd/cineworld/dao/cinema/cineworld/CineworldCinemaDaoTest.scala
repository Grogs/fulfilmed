package me.gregd.cineworld.dao.cinema.cineworld

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.FakeRatings
import me.gregd.cineworld.PostcodeService
import me.gregd.cineworld.config.MoviesConfig
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain._
import me.gregd.cineworld.util.{NoOpCache, RealClock}
import monix.execution.Scheduler
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs
import scala.concurrent.duration._

class CineworldCinemaDaoTest extends FunSuite with ScalaFutures with Matchers with Eventually {

  implicit val defaultPatienceConfig = PatienceConfig(Span(3000, Millis))

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val tmdb = new TheMovieDB(wsClient, NoOpCache.cache, Scheduler.global, Stubs.tmdb.config)
  val movieDao = new Movies(tmdb, FakeRatings, MoviesConfig(1.second))
  val cineworldRaw = new CineworldRepository(wsClient, NoOpCache.cache, Stubs.cineworld.config, RealClock)
  val postcodeService = new PostcodeService(Stubs.postcodesio.config, wsClient)
  val cineworld = new CineworldCinemaDao(movieDao, tmdb, cineworldRaw, postcodeService)

  test("retrieveCinemas") {
    val cinemas = cineworld.retrieveCinemas().futureValue.take(3)
    cinemas shouldEqual expectedCinemas
  }

  test("retrieveMoviesAndPerformances") {
    val date = LocalDate.parse("2017-05-23")
    val showings = cineworld.retrieveMoviesAndPerformances("8112", date).futureValue
    pprint.pprintln(showings, height = 100000)
    showings should contain allElementsOf expectedShowings
  }

  val expectedCinemas = List(
    Cinema("8014", "Cineworld", "Aberdeen - Queens Links", Option(Coordinates(57.1502699571208, -2.07796067079163))),
    Cinema("8018", "Cineworld", "Aberdeen - Union Square", Option(Coordinates(57.1443735096293, -2.09607620679942))),
    Cinema("8015", "Cineworld", "Aldershot", Option(Coordinates(51.2496276978637, -0.76918738639163)))
  )

  private val ticketBase = "https://www.cineworld.co.uk/ecom-tickets?siteId=8112&prsntId"
  private val postBase = "https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters"

  val expectedShowings: Map[Movie,List[Performance]] = Map(
    Movie("Coco",Some("ho00004273"),Some("default"), None, None, None, None, None, None, Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004273-md.jpg"), None, None) -> List(
      Performance("10:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46866",Some("2018-04-12"))
    ),
    Movie("Pacific Rim Uprising",Some("ho00005032"),Some("default"), None, None, None, None, None, None, Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005032-md.jpg"), None, None) -> List(
      Performance("12:30",true,"2D","https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46890",Some("2018-04-12")),
      Performance("15:10",true,"2D","https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46891",Some("2018-04-12")),
      Performance("17:50",true,"2D","https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46892",Some("2018-04-12")),
      Performance("20:50",true,"2D","https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46941",Some("2018-04-12")
      )
    ),
    Movie("Rampage",Some("ho00005044"),Some("default"), None, None, None, None, None, None, Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005044-md.jpg"), None, None) -> List(
      Performance("10:00", true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46918",Some("2018-04-12")
      ),
      Performance("11:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46933",Some("2018-04-12")),
      Performance("12:30", true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46919",Some("2018-04-12")),
      Performance("14:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46934",Some("2018-04-12")),
      Performance("15:00", true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46920",Some("2018-04-12")),
      Performance("17:15", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46935",Some("2018-04-12")),
      Performance("17:35", true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46921",Some("2018-04-12")),
      Performance("19:50", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46936",Some("2018-04-12")),
      Performance("20:20", true, "3D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46922",Some("2018-04-12")),
      Performance("21:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46931",Some("2018-04-12"))
    ),
    Movie("Duck Duck Goose",Some("ho00005039"),Some("default"), None, None, None, None, None, None, Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005039-md.jpg"), None, None) -> List(
      Performance("10:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46903",Some("2018-04-12")),
      Performance("13:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46904",Some("2018-04-12")),
      Performance("15:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46905",Some("2018-04-12"))
    ),
    Movie("A Quiet Place",Some("ho00005063"),Some("default"), None, None, None, None, None, None, Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005063-md.jpg"), None, None) -> List(
      Performance("11:45", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46881",Some("2018-04-12")),
      Performance("14:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46882",Some("2018-04-12")),
      Performance("16:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46883",Some("2018-04-12")),
      Performance("18:50", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46884",Some("2018-04-12")),
      Performance("20:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46875",Some("2018-04-12")),
      Performance("21:15", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46885",Some("2018-04-12"))
    ),
    Movie("The Boss Baby",Some("ho00004610"),Some("default"), None, None, None, None, None, None, Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004610-md.jpg"), None, None) -> List(
      Performance("10:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46917",Some("2018-04-12"))
    ),
    Movie("Molly's Game",Some("ho00004957"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004957-md.jpg"),None,None) -> List(
      Performance("11:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46940",Some("2018-04-12"))
    ),
    Movie("Thoroughbreds",Some("ho00005022"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005022-md.jpg"),None,None) -> List(
      Performance("16:45", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46888",Some("2018-04-12")),
      Performance("19:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46889",Some("2018-04-12"))
    ),
    Movie("Early Man",Some("ho00005313"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005313-md.jpg"),None,None) -> List(
      Performance("10:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46930",Some("2018-04-12"))
    ),
    Movie("Peter Rabbit",Some("ho00005028"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005028-md.jpg"),None,None) -> List(
      Performance("10:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46871",Some("2018-04-12")),
      Performance("10:50", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46876",Some("2018-04-12")),
      Performance("11:50", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46886",Some("2018-04-12")),
      Performance("12:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46872",Some("2018-04-12")),
      Performance("13:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46877",Some("2018-04-12")),
      Performance("14:15", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46887",Some("2018-04-12")),
      Performance("15:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46873",Some("2018-04-12")),
      Performance("15:50", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46878",Some("2018-04-12")),
      Performance("17:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46874",Some("2018-04-12")),
      Performance("18:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46879",Some("2018-04-12"))
    ),
    Movie("Tomb Raider",Some("ho00004888"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004888-md.jpg"),None,None) -> List(
      Performance("10:45", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46908",Some("2018-04-12")),
      Performance("14:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46909",Some("2018-04-12")),
      Performance("17:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46910",Some("2018-04-12")),
      Performance("20:45", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46911",Some("2018-04-12"))
    ),
    Movie(
      "The Greatest Showman",Some("ho00004787"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004787-md.jpg"),None,None
    ) -> List(
      Performance("12:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46926",Some("2018-04-12")),
      Performance("15:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46927",Some("2018-04-12")),
      Performance("17:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46928",Some("2018-04-12")),
      Performance("20:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46916",Some("2018-04-12"))
    ),
    Movie(
      "Death Wish",Some("ho00005261"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005261-md.jpg"),None,None
    ) -> List(
      Performance("17:45", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46906",Some("2018-04-12")),
      Performance("20:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46907",Some("2018-04-12"))
    ),
    Movie(
      "Love, Simon",Some("ho00005030"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005030-md.jpg"),None,None
    ) -> List(
      Performance("11:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46863",Some("2018-04-12")),
      Performance("14:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46864",Some("2018-04-12")),
      Performance("17:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46865",Some("2018-04-12")),
      Performance("19:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46912",Some("2018-04-12"))
    ),
    Movie(
      "Black Panther",Some("ho00004790"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00004790-md.jpg"),None,None
    ) -> List(
      Performance("10:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46895",Some("2018-04-12")),
      Performance("13:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46896",Some("2018-04-12")),
      Performance("16:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46897",Some("2018-04-12")),
      Performance("19:50", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46915",Some("2018-04-12"))
    ),
    Movie(
      "Blockers",Some("ho00005049"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005049-md.jpg"),None,None
    ) -> List(
      Performance("15:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46900",Some("2018-04-12")),
      Performance("18:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46901",Some("2018-04-12")),
      Performance("20:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46902",Some("2018-04-12"))
    ),
    Movie(
      "Tad The Explorer And The Secret Of King Midas",Some("ho00005292"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005292-md.jpg"),None,None
    ) -> List(
      Performance("10:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46914",Some("2018-04-12"))
    ),
    Movie(
      "Ready Player One",Some("ho00005037"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005037-md.jpg"),None,None
    ) -> List(
      Performance("10:45", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46924",Some("2018-04-12")),
      Performance("14:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46923",Some("2018-04-12")),
      Performance("17:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46929",Some("2018-04-12")),
      Performance("19:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46932",Some("2018-04-12")),
      Performance("20:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46925",Some("2018-04-12"))
    ),
    Movie(
      "A Wrinkle In Time",Some("ho00005035"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005035-md.jpg"),None,None
    ) -> List(
      Performance("10:15", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46898",Some("2018-04-12")),
      Performance("13:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46899",Some("2018-04-12"))
    ),
    Movie(
      "Isle Of Dogs",Some("ho00005040"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005040-md.jpg"),None,None
    ) -> List(
      Performance("12:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46913",Some("2018-04-12")),
      Performance("14:50", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46893",Some("2018-04-12")),
      Performance("17:20", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46894",Some("2018-04-12"))
    ),
    Movie(
      "Pitbull: Ostatni Pies (Polish)",Some("ho00005287"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005287-md.jpg"),None,None
    ) -> List(
      Performance("20:15", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=45700",Some("2018-04-12"))
    ),
    Movie(
      "Ghost Stories",Some("ho00005051"),Some("default"),None,None,None,None,None,None,Some("https://www.cineworld.co.uk//xmedia-cw/repo/feats/posters/HO00005051-md.jpg"),None,None
    ) -> List(
      Performance("13:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46867",Some("2018-04-12")),
      Performance("16:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46868",Some("2018-04-12")),
      Performance("18:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46869",Some("2018-04-12")),
      Performance("21:00", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46870",Some("2018-04-12"))
    )
  )


}
