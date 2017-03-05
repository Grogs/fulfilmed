package me.gregd.cineworld.dao.movies

import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton, Named => named}

import com.rockymadden.stringmetric.similarity.DiceSorensenMetric
import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.domain.{Film, Format, Movie}
import me.gregd.cineworld.util.Implicits._
import org.feijoas.mango.common.cache.CacheBuilder
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import scalaj.http.Http
import scalaj.http.HttpOptions.{connTimeout, readTimeout}

@Singleton
class Movies @Inject() (
                         @named("rotten-tomatoes.api-key") rottenTomatoesApiKey:String,
                         tmdb: TheMovieDB
                       ) extends MovieDao with Logging {
  
  implicit val formats = DefaultFormats

  val imdbCache = CacheBuilder.newBuilder()
    .refreshAfterWrite(24, TimeUnit.HOURS)
    .build( (id:String) => imdbRatingAndVotes(id) orElse imdbRatingAndVotes_new(id) )

  def getId(title:String) = find(title).flatMap(_.imdbId)
  def getIMDbRating(id:String) = imdbCache(id ).map(_._1)
  def getVotes(id:String) = imdbCache(id).map(_._2)

  def toMovie(film: Film): Movie = {
    logger.debug(s"Creating movie from $film")
    val format = Format.split(film.title)._1
    val movie: Movie = find(film.cleanTitle)
      .getOrElse(
        Movie(film.cleanTitle, None, None, None, None, None, None, None, None, None)
      )
      .copy(
        title = film.title,
        cineworldId = Option(film.id),
        format = Option(format),
        posterUrl = Option(film.poster_url)
      )
    val imdbId = movie.imdbId map ("tt" + _)
    val rating = imdbId flatMap getIMDbRating
    val votes = imdbId flatMap getVotes
    movie
      .copy(rating = rating, votes = votes)
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
    Await.result(cachedMovies, 5.seconds)
  }

  def allMovies(): Future[Seq[Movie]] = {
    for {
      tmdbNowPlaying <- tmdb.fetchNowPlaying()
    } yield {

      val movies = tmdbNowPlaying.map{ m =>
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
        _=logger.trace(s"Alternative title for ${m.title}: $altTitle")
      } yield m.copy(title = altTitle)

      (movies ++ alternateTitles) distinctBy (_.title)
    }
  }

  val compareFunc: (String, String) => Double =
    DiceSorensenMetric(1).compare(_:String,_:String).get

  val minWeight = 0.8

  def find(title: String): Option[Movie] = {
    val matc = allMoviesCached().maxBy(m => compareFunc(title,m.title))
    val weight = compareFunc(title, matc.title)
    logger.info(s"Best match for $title was  ${matc.title} ($weight) - ${if (weight>minWeight) "ACCEPTED" else "REJECTED"}")
    if (weight > minWeight) Option(matc) else None
  }

  protected[movies] def imdbRatingAndVotes(id:String): (Option[(Double,Int)]) = {
    logger.debug(s"Retreiving IMDb rating and votes for $id")
    val resp = Http(s"http://www.omdbapi.com/?i=$id")
      .options(connTimeout(30000), readTimeout(30000))
      .asString.body
    logger.debug(s"OMDb response for $id:\n$resp")
    val rating = Try(
      (parse(resp) \ "imdbRating").extract[String].toDouble
    ).toOption
    val votes = Try(
      (parse(resp) \ "imdbVotes").extract[String] match { //needed as ',' is used as decimal mark
        case s => NumberFormat.getIntegerInstance.parse(s).intValue
      }
    ).toOption
    logger.debug(s"$id: $rating with $votes votes")
    (rating,votes) match {
      case (Some(r), Some(v)) => Option(r,v)
      case _ => None
    }
  }

  protected[movies] def imdbRatingAndVotes_new(id:String): (Option[(Double,Int)]) = {
    logger.debug(s"Retreiving IMDb rating (v2) and votes for $id")
    val resp = Http("http://deanclatworthy.com/imdb/")
      .options(connTimeout(10000), readTimeout(10000))
      .params(
        "id" -> id
      )
      .asString.body
    logger.debug(s"IMDB API response for $id:\n$resp")

    val rating = Try(
      (parse(resp) \ "rating").extract[String].toDouble
    ).toOption
    val votes = Try(
      (parse(resp) \ "votes").extract[String].toInt
    ).toOption
    logger.debug(s"$id: $rating with $votes votes")
    val res: Option[(Double, Int)] = (rating,votes) match {
      case (Some(r), Some(v)) => Option(r,v)
      case _ => None
    }
    res
  }
}
