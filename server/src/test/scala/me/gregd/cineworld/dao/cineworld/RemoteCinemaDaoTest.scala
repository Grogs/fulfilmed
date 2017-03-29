package me.gregd.cineworld.dao.cineworld

import java.time.LocalDate

import fakes.{FakeCineworldRepository, FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}
import me.gregd.cineworld.util.FixedClock
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}

class RemoteCinemaDaoTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(1500, Millis))

  val movieDao = new Movies(FakeTheMovieDB, FakeRatings)
  val remoteCinemaDao = new RemoteCinemaDao(movieDao, FakeTheMovieDB, FakeCineworldRepository, FixedClock(LocalDate.parse("2017-03-23")))

  test("retrieveCinemas") {
    val cinemas = remoteCinemaDao.retrieveCinemas().futureValue
    cinemas shouldEqual expectedCinemas
  }

  test("retrieveMoviesAndPerformances") {
    val showings = remoteCinemaDao.retrieveMoviesAndPerformances(expectedCinemas.head.id, "2017-03-27").futureValue

    showings shouldEqual expectedShowings
  }

  val expectedCinemas = List(
    Cinema("1010900", "Harlow - Harvey Centre"),
    Cinema("1010886", "Yeovil"),
    Cinema("1010895", "Hinckley"),
    Cinema("1010873", "London - Staples Corner - CLOSED"),
    Cinema("1010884", "London - Wood Green"),
    Cinema("1010882", "London - West India Quay"),
    Cinema("1010813", "Bolton"),
    Cinema("1010868", "Basildon"),
    Cinema("1010897", "Yate"),
    Cinema("1010821", "Cardiff")
  )

  private val ticketBase = "https://www.cineworld.co.uk/ecom-tickets?siteId=1010900&prsntId="
  private val postBase = "https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters/"
  val expectedShowings = Map(
    Movie("Get Out", Some("HO00004242"), Some("default"), None, Some(419430.0), None, None, Some(7.0), Some(360), Some(postBase + "HO00004242.jpg")) -> List(
      Performance("12:50", true, "2D", ticketBase + "83182", Some("27/03/2017")),
      Performance("15:20", true, "2D", ticketBase + "83183", Some("27/03/2017")),
      Performance("18:30", true, "2D", ticketBase + "83268", Some("27/03/2017")),
      Performance("21:00", true, "2D", ticketBase + "83269", Some("27/03/2017"))
    ),
    Movie("Beauty And The Beast", Some("HO00004168"), Some("default"), None, Some(321612.0), None, None, Some(7.2), Some(537), Some(postBase + "HO00004168.jpg")) -> List(
      Performance("11:20", true, "2D", ticketBase + "83116", Some("27/03/2017")),
      Performance("12:40", true, "2D", ticketBase + "83282", Some("27/03/2017")),
      Performance("13:20", true, "2D", ticketBase + "83031", Some("27/03/2017")),
      Performance("14:20", true, "2D", ticketBase + "83117", Some("27/03/2017")),
      Performance("15:40", true, "2D", ticketBase + "83283", Some("27/03/2017")),
      Performance("16:20", true, "2D", ticketBase + "83032", Some("27/03/2017")),
      Performance("17:20", true, "2D", ticketBase + "83118", Some("27/03/2017")),
      Performance("18:00", true, "2D", ticketBase + "83152", Some("27/03/2017")),
      Performance("20:15", true, "2D", ticketBase + "83119", Some("27/03/2017"))
    ),
    Movie("Life (2017)", Some("HO00004250"), Some("default"), None, None, None, None, None, None, Some(postBase + "HO00004250.jpg")) -> List(
      Performance("12:10", true, "2D", ticketBase + "83054", Some("27/03/2017")),
      Performance("14:40", true, "2D", ticketBase + "83055", Some("27/03/2017")),
      Performance("17:15", true, "2D", ticketBase + "83056", Some("27/03/2017")),
      Performance("19:45", true, "2D", ticketBase + "83057", Some("27/03/2017"))
    )
  )
}
