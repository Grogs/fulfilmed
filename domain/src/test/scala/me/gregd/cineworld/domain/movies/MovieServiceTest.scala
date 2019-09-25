package me.gregd.cineworld.domain.movies

import me.gregd.cineworld.integration.omdb.{OmdbIntegrationService, RatingsResult}
import me.gregd.cineworld.domain.model.{Film, Movie}
import me.gregd.cineworld.domain.service.MovieService
import me.gregd.cineworld.integration.tmdb.{TmdbIntegrationService, TmdbMovie}
import me.gregd.cineworld.config.MoviesConfig
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Span}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._

class MovieServiceTest extends FunSuite with ScalaFutures with Matchers with MockFactory {

  implicit val defaultPatienceConfig = PatienceConfig(Span(2500, Millis))

  val tmdb = stub[TmdbIntegrationService]
  val ratings = stub[OmdbIntegrationService]

  (tmdb.fetchMovies _).when().returns(Future.successful(Vector(someTmdbMovie)))
  (tmdb.fetchImdbId _).when(*).onCall{(id: String) => id match {
    case "263115" => Future.successful(Some("tt3315342"))
    case _ => Future.successful(None)
  }}
  (tmdb.alternateTitles _).when(*).returns(Future.successful(Nil))

  (ratings.fetchRatings _).when(*).returns(Future.successful(RatingsResult(Some(7.1), Some(59166), None, None)))

  val movieDao = new MovieService(tmdb, ratings, MoviesConfig(1.second))

  test("unknown film can be converted to movie") {
    val film = Film("1", "Test", "")
    val movie = movieDao.toMovie(film).futureValue
    movie shouldBe Movie("Test", Some("1"), Some("default"))
  }

  test("known film can be converted to movie") {
    val film = Film("2", "Logan", "")
    val movie = movieDao.toMovie(film).futureValue
    movie shouldBe someMovie
  }

  test("allMovies") {
    val movies = movieDao.allMoviesCached().futureValue
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
