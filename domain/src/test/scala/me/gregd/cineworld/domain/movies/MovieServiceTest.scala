package me.gregd.cineworld.domain.movies

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import me.gregd.cineworld.integration.omdb.{OmdbIntegrationService, OmdbService, RatingsResult}
import me.gregd.cineworld.domain.model.{Film, Movie}
import me.gregd.cineworld.domain.service.MovieService
import me.gregd.cineworld.integration.tmdb.{TmdbIntegrationService, TmdbMovie, TmdbService}
import me.gregd.cineworld.config.MoviesConfig
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.time.{Millis, Span}
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future
import scala.concurrent.duration._

class MovieServiceTest extends AnyFunSuite with ScalaFutures with Matchers with MockFactory {

  implicit val defaultPatienceConfig = PatienceConfig(Span(2500, Millis))

  val tmdb = new TmdbService {
    val baseImageUrl: String = "http://image.tmdb.org/t/p/w300"
    def fetchMovies()        = IO.pure(Vector(someTmdbMovie))
    def fetchImdbId(tmdbId: String) = tmdbId match {
      case "263115" => IO.pure(Some("tt3315342"))
      case _        => IO.pure(None)
    }
    def alternateTitles(tmdbId: String) = IO.pure(Vector.empty)
  }
  val ratings = new OmdbService {
    def fetchRatings(imdbId: String) = IO.pure(RatingsResult(Some(7.1), Some(59166), None, None))
  }

  val movieService = new MovieService(tmdb, ratings, MoviesConfig(1.second))
  movieService.refresh.unsafeRunSync()

  test("unknown film can be converted to movie") {
    val film  = Film("1", "Test", "")
    val movie = movieService.toMovie(film).unsafeRunSync()
    movie shouldBe Movie("Test", Some("1"), Some("default"))
  }

  test("known film can be converted to movie") {
    val film  = Film("2", "Logan", "")
    val movie = movieService.toMovie(film).unsafeRunSync()
    movie shouldBe someMovie
  }

  test("allMovies") {
    val movies = movieService.allMoviesCached().unsafeRunSync()
    movies shouldEqual Vector(someMovie.copy(cineworldId = None, format = None))
  }

  private val someMovie = Movie(
    title = "Logan",
    cineworldId = Some("2"),
    format = Some("default"),
    imdbId = Some("tt3315342"),
    tmdbId = Some("263115"),
    rating = Some(7.1),
    votes = Some(59166),
    tmdbRating = Some(7.7),
    tmdbVotes = Some(1648),
    posterUrl = Some("http://image.tmdb.org/t/p/w300/45Y1G5FEgttPAwjTYic6czC9xCn.jpg")
  )

  def someTmdbMovie = TmdbMovie(
    poster_path = Some("/45Y1G5FEgttPAwjTYic6czC9xCn.jpg"),
    adult = false,
    overview = "In the near future, a weary Logan cares ",
    release_date = "2017-03-02",
    genre_ids = List(28.0, 18.0, 878.0),
    id = 263115,
    original_title = "Logan",
    original_language = "en",
    title = "Logan",
    backdrop_path = Some("/5pAGnkFYSsFJ99ZxDIYnhQbQFXs.jpg"),
    popularity = 134.791894,
    vote_count = 1648.0,
    video = false,
    vote_average = 7.7
  )

}
