package me.gregd.cineworld.dao.movies

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton, Named => named}

import com.rockymadden.stringmetric.similarity.DiceSorensenMetric
import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.domain.{Film, Format, Movie}
import me.gregd.cineworld.util.Implicits._
import org.feijoas.mango.common.cache.CacheBuilder
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class Movies @Inject()(tmdb: TheMovieDB, ratings: Ratings) extends MovieDao with Logging {

  implicit val formats = DefaultFormats

  val imdbCache = CacheBuilder
    .newBuilder()
    .refreshAfterWrite(24, TimeUnit.HOURS)
    .build((id: String) => ratings.imdbRatingAndVotes(id))

  def getIMDbRating(id: String) = imdbCache(id).map(_._1)
  def getVotes(id: String) = imdbCache(id).map(_._2)

  def toMovie(film: Film): Future[Movie] = {
    logger.debug(s"Creating movie from $film")
    for (movieOpt <- find(film.cleanTitle)) yield {
      val format = Format.split(film.title)._1
      val movie = movieOpt.getOrElse(Movie(film.title))
      val imdbId = movie.imdbId map ("tt" + _)
      val rating = imdbId flatMap getIMDbRating
      val votes = imdbId flatMap getVotes
      movie.copy(
        title = film.title,
        cineworldId = Option(film.id),
        format = Option(format),
        posterUrl = Option(film.poster_url),
        rating = rating,
        votes = votes
      )
    }
  }

  private var cachedMovies: Future[Seq[Movie]] = Future.failed(new UninitializedError)
  private var lastCached: Long = 0
  def allMoviesCached() = {
    def refresh = {
      logger.info(s"refreshing movies cache, old value:\n$cachedMovies")
      cachedMovies = allMovies()
      lastCached = System.currentTimeMillis()
    }
    val age = System.currentTimeMillis() - lastCached
    if (age.millis > 10.hours) refresh
    cachedMovies
  }

  def allMovies(): Future[Seq[Movie]] = {
    for {
      tmdbNowPlaying <- tmdb.fetchNowPlaying()
    } yield {

      val movies = tmdbNowPlaying.map { m =>
        Movie(
          m.title,
          None,
          None,
          None,
          Option(m.id),
          None,
          None,
          Option(m.vote_average),
          Option(m.vote_count.toInt),
          m.poster_path.map(tmdb.baseImageUrl + _)
        )
      }

      val alternateTitles = for {
        m <- movies
        altTitle <- tmdb.alternateTitles(m)
        _ = logger.trace(s"Alternative title for ${m.title}: $altTitle")
      } yield m.copy(title = altTitle)

      (movies ++ alternateTitles) distinctBy (_.title)
    }
  }

  val compareFunc: (String, String) => Double =
    DiceSorensenMetric(1).compare(_: String, _: String).get

  val minWeight = 0.8

  def find(title: String): Future[Option[Movie]] = for (allMovies <- allMoviesCached()) yield {
    val matc = allMovies.maxBy(m => compareFunc(title, m.title))
    val weight = compareFunc(title, matc.title)
    logger.debug(s"Best match for $title was  ${matc.title} ($weight) - ${if (weight > minWeight) "ACCEPTED" else "REJECTED"}")
    if (weight > minWeight) Option(matc) else None
  }

}
