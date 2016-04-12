package me.gregd.cineworld.dao

import javax.inject.{Named=>named, Inject, Singleton}

import scalaj.http.{HttpOptions, Http, HttpRequest}
import org.json4s._
import org.json4s.native.JsonMethods._
import me.gregd.cineworld.domain.Movie
import scala.util.Try
import me.gregd.cineworld.util.Implicits._
import grizzled.slf4j.Logging
import me.gregd.cineworld.Config

/**
 * Created by Greg Dorrell on 22/05/2014.
 */
@Singleton
class TheMovieDB @Inject() (@named("themoviedb.api-key")apiKey: String) extends Logging {

  protected implicit val formats = DefaultFormats

  val baseUrl="http://api.themoviedb.org/3"
  val baseImageUrl = {
    val json =  get("configuration")
    (json \ "images" \ "base_url").extract[String] + "w300"
  }

  protected def get(path:String, transform: HttpRequest => HttpRequest = m => m): JValue = {
    val req = transform(
      Http(s"$baseUrl/$path")
        .option(HttpOptions.connTimeout(30000))
        .option(HttpOptions.readTimeout(30000))
        .header("Accept", "application/json")
        .param("api_key", apiKey)
    )
    logger.debug(s"Getting: ${req.url} with params ${req.params}")
    val resp = req.asString
    parse(StringInput(resp.body))
  }

  def alternateTitles(imdbId: String): Seq[String] = Try {
    val json = get(s"movie/tt$imdbId/alternative_titles")
    val titles = json \ "titles" \ "title"
    titles
      .extractOrElse[Seq[String]](
        Seq(titles.extract[String])
      )
  }
    .onFailure(logger.error(s"Unable to retrieve alternate titles for $imdbId from TMDB",_:Throwable))
    .getOrElse(Nil)

  def posterUrl(m: Movie): Option[String] = m.imdbId flatMap posterUrl
  def alternateTitles(m: Movie): Seq[String] = (m.imdbId map alternateTitles) getOrElse Nil



  def posterUrl(imdbId: String): Option[String] = Try {
    val json = get(s"find/tt$imdbId", _.param("external_source","imdb_id"))
    logger.debug(json \ "movie_results" \ "poster_path")
    val imageName = (json \ "movie_results" \ "poster_path").extract[String]
    s"$baseImageUrl$imageName"
  }
    .onFailure(logger.error(s"Unable to retrieve poster for $imdbId from TMDB",_:Throwable))
    .toOption

}

//object TheMovieDB extends TheMovieDB(Config.tmdbApiKey) {}
