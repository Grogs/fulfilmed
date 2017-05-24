package me.gregd.cineworld.dao.cinema.cineworld

import fakes.{FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.cinema.cineworld.raw.CineworldRepository
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.domain.{Cinema, Movie, Performance}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}
import stub.Stubs

class CineworldCinemaDaoTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(2000, Millis))

  val movieDao = new Movies(FakeTheMovieDB, FakeRatings)
  def cineworldCinemaDao(repo: CineworldRepository) = new CineworldCinemaDao(movieDao, FakeTheMovieDB, repo)
  def withCineworldCinemaDao[T](f: CineworldCinemaDao => T) = Stubs.withStubbedCineworld(cineworldCinemaDao _ andThen f)

  test("retrieveCinemas")(withCineworldCinemaDao { cineworld =>
    val cinemas = cineworld.retrieveCinemas().futureValue.take(3)
    cinemas shouldEqual expectedCinemas
  })

  test("retrieveMoviesAndPerformances")(withCineworldCinemaDao { cineworld =>
    val showings = cineworld.retrieveMoviesAndPerformances("1010882", "2017-05-23").futureValue.take(3)
    showings shouldEqual expectedShowings
  })

  val expectedCinemas = List(Cinema("1010804", "Aberdeen - Queens Links"), Cinema("1010808", "Aberdeen - Union Square"), Cinema("1010805", "Aldershot"))

  private val ticketBase = "https://www.cineworld.co.uk/ecom-tickets?siteId=1010882&prsntId"
  private val postBase = "https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters"

  val expectedShowings = Map(
    Movie("Half Girlfriend (Hindi)", Some("HO00004553"), Some("default"), None, None, None, None, None, None, Some(s"$postBase/HO00004553.jpg")) -> List(
      Performance("20:00", true, "2D", s"$ticketBase=85595", Some("23/05/2017"))
    ),
    Movie("Whisky Galore!", Some("HO00004360"), Some("default"), None, None, None, None, None, None, Some(s"$postBase/HO00004360.jpg")) -> List(
      Performance("11:20", true, "2D", s"$ticketBase=85584", Some("23/05/2017")),
      Performance("15:20", true, "2D", s"$ticketBase=85674", Some("23/05/2017"))
    ),
    Movie("Guardians Of The Galaxy Vol. 2", Some("HO00004330"), Some("default"), None, None, None, None, None, None, Some(s"$postBase/HO00004330.jpg")) -> List(
      Performance("11:20", true, "2D", s"$ticketBase=85588", Some("23/05/2017")),
      Performance("14:20", true, "2D", s"$ticketBase=85589", Some("23/05/2017")),
      Performance("17:20", true, "2D", s"$ticketBase=85590", Some("23/05/2017")),
      Performance("20:20", true, "2D", s"$ticketBase=85591", Some("23/05/2017"))
    )
  )
}
