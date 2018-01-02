package me.gregd.cineworld.dao.cinema.cineworld

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import fakes.{FakeRatings, NoOpCache}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain._
import monix.execution.Scheduler
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.ws.ahc.AhcWSClient
import stub.Stubs

class CineworldCinemaDaoTest extends FunSuite with ScalaFutures with Matchers with Eventually {

  implicit val defaultPatienceConfig = PatienceConfig(Span(3000, Millis))

  val wsClient = AhcWSClient()(ActorMaterializer()(ActorSystem()))
  val tmdb = new TheMovieDB(wsClient, NoOpCache.cache, Scheduler.global, Stubs.tmdb.config)
  val movieDao = new Movies(tmdb, FakeRatings)
  val cineworldRaw = new CineworldRepository(wsClient, NoOpCache.cache, Stubs.cineworld.config)
  val cineworld = new CineworldCinemaDao(movieDao, tmdb, cineworldRaw)

  test("retrieveCinemas") {
    val cinemas = cineworld.retrieveCinemas().futureValue.take(3)
    cinemas shouldEqual expectedCinemas
  }

  test("retrieveMoviesAndPerformances") {
    val showings = cineworld.retrieveMoviesAndPerformances("1010882", "2017-05-23").futureValue
    showings should contain allElementsOf expectedShowings
  }

  val expectedCinemas = List(
    Cinema("1010804", "Cineworld", "Aberdeen - Queens Links", Option(Coordinates(57.1503, -2.07796))),
    Cinema("1010808", "Cineworld", "Aberdeen - Union Square", Option(Coordinates(57.1436, -2.0969))),
    Cinema("1010805", "Cineworld", "Aldershot", Option(Coordinates(51.25, -0.768377)))
  )

  private val ticketBase = "https://www.cineworld.co.uk/ecom-tickets?siteId=1010882&prsntId"
  private val postBase = "https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters"

  val expectedShowings = Map(
    Movie("Half Girlfriend", Some("HO00004553"), Some("default"), None, None, None, None, None, None, Some(s"$postBase/HO00004553.jpg")) -> List(
      Performance("20:00", true, "2D", s"$ticketBase=85595", Some("23/05/2017"))
    ),
    Movie("Whisky Galore!", Some("HO00004360"), Some("default"), None, None, None, None, None, None, Some(s"$postBase/HO00004360.jpg")) -> List(
      Performance("11:20", true, "2D", s"$ticketBase=85584", Some("23/05/2017")),
      Performance("15:20", true, "2D", s"$ticketBase=85674", Some("23/05/2017"))
    ),
    Movie("The Secret Scripture", Some("HO00004373"), Some("default"), None, None, None, None, None, None, Some(s"$postBase/HO00004373.jpg")) -> List(
      Performance("11:00", true, "2D", s"$ticketBase=85687", Some("23/05/2017")),
      Performance("13:30", true, "2D", s"$ticketBase=85688", Some("23/05/2017")),
      Performance("20:50", true, "2D", s"$ticketBase=85695", Some("23/05/2017"))
    )
  )

}
