package me.gregd.cineworld.dao.movies

import fakes.{FakeCineworldRepository, FakeTheMovieDB}
import me.gregd.cineworld.dao.cineworld.CineworldRepository.toFilm
import me.gregd.cineworld.domain.Movie
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}

class MoviesTest extends FunSuite with ScalaFutures with Matchers {

  val movieDao = new Movies(FakeTheMovieDB)

  test("movies.toMovie") {
    val cinema = FakeCineworldRepository.retrieveCinemas().futureValue.head

    val films = FakeCineworldRepository
      .retrieve7DayListings(cinema.id.toString)
      .futureValue
      .map(toFilm)

    val movies = films.map(movieDao.toMovie)

    movies shouldEqual expectedFilmToMovies
  }

  test("allMovies") {
    val movies = movieDao.allMovies().futureValue.take(3)
    movies shouldEqual expectedAllMovies
  }

  private val posterBase = "https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters/"
  val expectedFilmToMovies = List(
    Movie("Get Out", Some("HO00004242"), Some("default"), None, Some(419430.0), None, None, Some(7.0), Some(360), Some(posterBase + "HO00004242.jpg")),
    Movie("Fast & Furious 8", Some("HO00004170"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004170.jpg")),
    Movie("Beauty And The Beast", Some("HO00004168"), Some("default"), None, Some(321612.0), None, None, Some(7.2), Some(537), Some(posterBase + "HO00004168.jpg")),
    Movie("Sing", Some("HO00004050"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004050.jpg")),
    Movie("Angamaly Diaries (Malayalam)", Some("HO00004416"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004416.jpg")),
    Movie("Life (2017)", Some("HO00004250"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004250.jpg")),
    Movie("Kung Fu Panda 3 - Subtitiled Movies For Juniors", Some("HO00003561"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00003561.jpg")),
    Movie("Ghost In The Shell", Some("HO00004247"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004247.jpg")),
    Movie("Peppa Pig: My First Cinema Experience", Some("HO00004204"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004204.jpg")),
    Movie("Trolls: Movies For Juniors", Some("HO00004149"), Some("default"), None, None, None, None, None, None, Some(posterBase + "HO00004149.jpg"))
  )

  val expectedAllMovies = List(
    Movie("Patriots Day", None, None, None, Some(388399.0), None, None, Some(6.7), Some(170), Some("http://image.tmdb.org/t/p/w300/cDbEiJIRwFcx2GsClJ1hDUY5Vwj.jpg")),
    Movie("Power Rangers",None,None,None,Some(305470.0),None,None,Some(7.8),Some(9),Some("http://image.tmdb.org/t/p/w300/y5KrW9mxeUmxUIYwNZOgnkYKQ8y.jpg")),
    Movie("A Cure for Wellness",None,None,None,Some(340837.0),None,None,Some(5.5),Some(170),Some("http://image.tmdb.org/t/p/w300/byeTgTgG7M1RN2c7njWWIkSkNig.jpg"))
  )

}
