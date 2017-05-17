package me.gregd.cineworld.dao.cinema

import java.time.LocalDate

import fakes.{FakeCineworldRepository, FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.cinema.cineworld.CineworldCinemaDao
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}
import me.gregd.cineworld.util.FixedClock
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Future

class CachingCinemaDaoTest extends FunSuite with ScalaFutures with Matchers  {

  implicit val defaultPatienceConfig = PatienceConfig(Span(1500, Millis))

  val fakeRemoteCinemaDao = new CineworldCinemaDao(new Movies(FakeTheMovieDB, FakeRatings), FakeTheMovieDB, FakeCineworldRepository, FixedClock(LocalDate.parse("2017-03-23")))

  val cachingCinemaDao = new CachingCinemaDao(fakeRemoteCinemaDao, monix.execution.Scheduler.global, FixedClock(LocalDate.parse("2017-03-23"))) {
    var refreshCinemaInvocations = 0
    var refreshListingsInvocations = 0

    override def refreshCinemas() = {
      refreshCinemaInvocations += 1
      super.refreshCinemas()
    }

    override def refreshListings(eventualCinemas: Future[Seq[Cinema]]) = {
      refreshListingsInvocations += 1
      super.refreshListings(eventualCinemas)
    }
  }

  test("retrieveCinemas") {
    def cinemas = cachingCinemaDao.retrieveCinemas().futureValue
    cinemas shouldBe expectedCinemas
    cachingCinemaDao.refreshCinemaInvocations shouldBe 0
    cinemas shouldBe expectedCinemas
    cachingCinemaDao.refreshCinemaInvocations shouldBe 0
  }

  test("retrieveMoviesAndPerformances") {
    def listings = cachingCinemaDao.retrieveMoviesAndPerformances("1010900", "2017-03-24").futureValue
    listings shouldBe expectedListings
    cachingCinemaDao.refreshListingsInvocations shouldBe 0
    listings shouldBe expectedListings
    cachingCinemaDao.refreshListingsInvocations shouldBe 0

  }

  private val expectedCinemas = List(
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
  private val expectedListings = Map(
    Movie("Get Out", Some("HO00004242"), Some("default"), None, Some("419430"), None, None, Some(7.0), Some(360), Some(postBase + "HO00004242.jpg")) -> List(
      Performance("12:50", true, "2D", ticketBase + "83180", Some("24/03/2017")),
      Performance("15:20", true, "2D", ticketBase + "83181", Some("24/03/2017")),
      Performance("18:30", true, "2D", ticketBase + "83262", Some("24/03/2017")),
      Performance("21:00", true, "2D", ticketBase + "83263", Some("24/03/2017"))
    ),
    Movie("Beauty And The Beast", Some("HO00004168"), Some("default"), None, Some("321612"), None, None, Some(7.2), Some(537), Some(postBase + "HO00004168.jpg")) -> List(
      Performance("11:20", true, "2D", ticketBase + "83104", Some("24/03/2017")),
      Performance("12:40", true, "2D", ticketBase + "83276", Some("24/03/2017")),
      Performance("13:20", true, "2D", ticketBase + "83025", Some("24/03/2017")),
      Performance("14:20", true, "2D", ticketBase + "83105", Some("24/03/2017")),
      Performance("15:40", true, "2D", ticketBase + "83277", Some("24/03/2017")),
      Performance("16:20", true, "2D", ticketBase + "83026", Some("24/03/2017")),
      Performance("17:20", true, "2D", ticketBase + "83106", Some("24/03/2017")),
      Performance("18:00", true, "2D", ticketBase + "83151", Some("24/03/2017")),
      Performance("20:15", true, "2D", ticketBase + "83107", Some("24/03/2017"))
    ),
    Movie("Life (2017)", Some("HO00004250"), Some("default"), None, None, None, None, None, None, Some(postBase + "HO00004250.jpg")) -> List(
      Performance("12:10", true, "2D", ticketBase + "83042", Some("24/03/2017")),
      Performance("14:40", true, "2D", ticketBase + "83043", Some("24/03/2017")),
      Performance("17:15", true, "2D", ticketBase + "83044", Some("24/03/2017")),
      Performance("19:45", true, "2D", ticketBase + "83045", Some("24/03/2017"))
    )
  )
}
