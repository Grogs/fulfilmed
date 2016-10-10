package me.gregd.cineworld.dao.movies

import javax.inject.{Inject, Singleton, Named => named}

import scala.util.Try
import scalaj.http.{Http, HttpOptions}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.feijoas.mango.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit._

import grizzled.slf4j.Logging
import java.text.NumberFormat

import com.rockymadden.stringmetric.similarity.DiceSorensenMetric
import me.gregd.cineworld.domain.{Film, Format, Movie}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.util.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaj.http.HttpOptions.{connTimeout, readTimeout}

@Singleton
class Movies @Inject() (
                         @named("rotten-tomatoes.api-key") rottenTomatoesApiKey:String,
                         tmdb: TheMovieDB
                       ) extends MovieDao with Logging {
  
  implicit val formats = DefaultFormats

  val imdbCache = CacheBuilder.newBuilder()
    .refreshAfterWrite(24, HOURS)
    .build( (id:String) => imdbRatingAndVotes(id) orElse imdbRatingAndVotes_new(id) )

  def getIMDbRating(id:String) = imdbCache(id ).map(_._1)
  def getVotes(id:String) = imdbCache(id).map(_._2)

  def toMovie(film: Film): Future[Movie] = for (movieOpt <- find(film.cleanTitle)) yield {
    logger.debug(s"Creating movie from $film")
    val format = Format.split(film.title)._1
    val movie = movieOpt
      .getOrElse(
        Movie(film.cleanTitle, None, None, None, None, None, None, None, None)
      )
      .copy(
        title = film.title,
        cineworldId = Option(film.id),
        format = Option(format),
        posterUrl = Option(film.poster_url)
      )
    val imdbId: Option[String] = movie.imdbId map ("tt" + _)
    val rating = imdbId flatMap getIMDbRating
    val votes = imdbId flatMap getVotes
    val posterUrl: Option[String] = Try(tmdb.posterUrl(movie)).toOption.flatten
    posterUrl match {
      case Some(newUrl) => logger.debug(s"Found highres poster in TMDD for '${movie.title}': $newUrl")
      case None => logger.debug(s"Didn't find poster in TMDB postUrl for ${movie.title}")
    }
    movie
      .copy(rating = rating, votes = votes)
      //Use higher res poster for TMDB when available
      .copy(posterUrl = posterUrl orElse movie.posterUrl)
  }


  private var cachedMovies: Future[Seq[Movie]] = Future(allMovies())
  def allMoviesCached() = {
//    def refresh = {
//      logger.info(s"refreshing movies cache")
//      val newValues = allMovies()
//      cachedMovies.completeWith(newValues)
//      newValues
//    }
    cachedMovies
  }

  def allMovies(): Seq[Movie] = {
    val movies = ( nowShowing ++ openingSoon ++ upcoming ) map { rt =>
      Movie(
        rt.title,
        None,
        None,
        rt.alternate_ids.imdb,
        None,
        None,
        rt.ratings.audience_score,
        rt.ratings.critics_score,
        None
      )
    }

    val alternateTitles = for {
      m <- movies
      altTitle <- tmdb.alternateTitles(m)
      _=logger.trace(s"Alternative title for ${m.title}: $altTitle")
    } yield m.copy(title = altTitle)

    (movies ++ alternateTitles) distinctBy (_.title)
  }

  val diceSorensen = DiceSorensenMetric(1).compare(_:String,_:String).get
  val minWeight = 0.8

  def find(title: String): Future[Option[Movie]] = for (moviesCache <- allMoviesCached()) yield{
    val matc = moviesCache.maxBy( m => diceSorensen(title, m.title) )
    val weight = diceSorensen(title, matc.title)
    logger.info(s"Best match for $title was  ${matc.title} ($weight) - ${if (weight>minWeight) "ACCEPTED" else "REJECTED"}")
    if (weight > minWeight) Option(matc) else None
  }

  /**
   * Uses Rotten Tomatoes In Theaters api call to get a Seq all of movies in UK cinemas.
   * Retrieves their IMDb ID, and audience/critic rating, as well as posters etc.
   *
   * Rotten tomatoes limits the call to 50 movies per page. So there isa  recursive call to retrieve all pages.
   */
  def nowShowing(): Seq[RTMovie] = {
    def acc(pageNum:Int = 1): Seq[RTMovie] = {
      logger.debug(s"Retreiving list of movies in threatres according to RT (page $pageNum)")
      val resp = Http("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json")
        .option(connTimeout(30000))
        .option(readTimeout(30000))
        .params(
          "apikey" -> rottenTomatoesApiKey,
          "country"-> "uk",
          "page_limit" -> "50",
          "page" -> pageNum.toString
        )
        .asString.body
      logger.debug(s"RT in_theaters page $pageNum:\n$resp")
      val json = parse(resp)
      val movies = (json \ "movies").extract[Seq[RTMovie]]
      if (movies.size < 50 || pageNum > 15) movies else movies ++ acc(pageNum+1)
    }
    acc()
  }
  
  def upcoming(): Seq[RTMovie] = {
    def acc(pageNum:Int = 1): Seq[RTMovie] = {
      logger.debug(s"Retreiving list of upcoming movies according to RT (page $pageNum)")
      val resp = Http("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json")
        .option(connTimeout(30000))
        .option(readTimeout(30000))
        .params(
          "apikey" -> rottenTomatoesApiKey,
          "country"-> "uk",
          "page_limit" -> "50",
          "page" -> pageNum.toString
        )
        .asString.body
      logger.debug(s"RT upcoming page $pageNum:\n$resp")
      val json = parse(resp)
      val movies = (json \ "movies").extract[Seq[RTMovie]]
      if (movies.size < 50 || pageNum > 15) movies else movies ++ acc(pageNum+1)
    }
    acc()
  }

  def openingSoon(): Seq[RTMovie] = {
      logger.debug(s"Retreiving list of movies opening this coming week according to RT")
      val resp = Http("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/opening.json")
        .option(connTimeout(30000))
        .option(readTimeout(30000))
        .params(
          "apikey" -> rottenTomatoesApiKey,
          "country"-> "uk",
          "limit" -> "50"
        )
        .asString.body
      logger.debug(s"RT opening:\n$resp")
      (parse(resp) \ "movies").extract[Seq[RTMovie]]
  }

  protected[movies] def imdbRatingAndVotes(id:String): (Option[(Double,Int)]) = {
    logger.debug(s"Retreiving IMDb rating and votes for $id")
    val resp = curl(s"http://www.omdbapi.com/?i=$id")
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
      .option(connTimeout(10000))
      .option(readTimeout(10000))
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
    (rating,votes) match {
      case (Some(r), Some(v)) => Option(r,v)
      case _ => None
    }
  }


  private def curl(url: String) = Http(url)
    .option(connTimeout(30000))
    .option(readTimeout(30000))
    .asString.body

}