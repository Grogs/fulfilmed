package me.gregd.cineworld.dao.movies

import fakes.{FakeCineworldRepository, FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.cineworld.CineworldRepository.toFilm
import me.gregd.cineworld.domain.Movie
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MoviesTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatienceConfig = PatienceConfig(Span(2500, Millis))

  val movieDao = new Movies(FakeTheMovieDB, FakeRatings)

  test("movies.toMovie") {
    val cinema = FakeCineworldRepository.retrieveCinemas().futureValue.head

    val films = FakeCineworldRepository
      .retrieve7DayListings(cinema.id.toString)
      .futureValue
      .map(toFilm)

    val movies = Future.traverse(films)(movieDao.toMovie).futureValue

    movies shouldBe expectedFilmToMovies
  }

  test("allMovies") {
    val movies = movieDao.allMoviesCached().futureValue.take(3)
    movies shouldEqual expectedFirstThreeMovies
  }

  val posterBase = "https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters/"
  val expectedFilmToMovies = Vector(
    Movie("Get Out", Some("HO00004242"), Some("default"), None, Some("419430"), None, None, Some(7.0), Some(360), Some(posterBase + "HO00004242.jpg")),
    Movie("Fast & Furious 8", Some("HO00004170"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004170.jpg")),
    Movie("Beauty And The Beast", Some("HO00004168"), Some("default"), None, Some("321612"), None, None, Some(7.2), Some(537), Some(posterBase + "HO00004168.jpg")),
    Movie("Sing", Some("HO00004050"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004050.jpg")),
    Movie("Angamaly Diaries (Malayalam)", Some("HO00004416"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004416.jpg")),
    Movie("Life (2017)", Some("HO00004250"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004250.jpg")),
    Movie("Kung Fu Panda 3 - Subtitiled Movies For Juniors", Some("HO00003561"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00003561.jpg")),
    Movie("Ghost In The Shell", Some("HO00004247"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004247.jpg")),
    Movie("Peppa Pig: My First Cinema Experience", Some("HO00004204"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004204.jpg")),
    Movie("Trolls: Movies For Juniors", Some("HO00004149"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004149.jpg"))
  )

  val tmdbImageBase = "http://image.tmdb.org/t/p/w300/"
  val expectedFirstThreeMovies = Vector(
    Movie("Logan", None, None, None, Some("263115"), None, None, Some(7.7), Some(1648), Some(tmdbImageBase + "45Y1G5FEgttPAwjTYic6czC9xCn.jpg")),
    Movie("Kong: Skull Island", None, None, None, Some("293167"), None, None, Some(6.1), Some(621), Some(tmdbImageBase + "aoUyphk4nwffrwlZRaOa0eijgpr.jpg")),
    Movie("Beauty and the Beast", None, None, None, Some("321612"), None, None, Some(7.2), Some(537), Some(tmdbImageBase + "tnmL0g604PDRJwGJ5fsUSYKFo9.jpg"))
  )
}
