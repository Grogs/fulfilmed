package me.gregd.cineworld.dao

import scalaj.http.{HttpOptions, Http}
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
class TheMovieDB(apiKey: String) extends Logging {
  protected implicit val formats = DefaultFormats

  val baseUrl="http://api.themoviedb.org/3"
  val baseImageUrl = {
    val json =  get("configuration")
    (json \ "images" \ "base_url").extract[String] + "w300"
  }

  protected def get(path:String, transform: Http.Request => Http.Request = (m=>m)): JValue = {
    val resp = transform(
        Http(s"$baseUrl/$path")
        .option(HttpOptions.connTimeout(30000))
        .option(HttpOptions.readTimeout(30000))
        .header("Accept", "application/json")
        .param("api_key", apiKey)
      )
      .asString
    parse(resp)
  }

  def alternateTitles(imdbId: String): Seq[String] = Try {
    val json = get(s"movie/$imdbId/alternative_titles")
    (json \ "titles" \ "title").extract[Seq[String]]
  }
    .onFailure(logger.error(s"Unable to retrieve alternate titles for $imdbId from TMDB",_:Throwable))
    .getOrElse(Nil)

  def posterUrl(m: Movie): Option[String] = m.imdbId flatMap posterUrl
  def alternateTitles(m: Movie): Seq[String] = (m.imdbId map alternateTitles) getOrElse Nil



  def posterUrl(imdbId: String): Option[String] = Try {
    val json = get(s"find/tt$imdbId", _.param("external_source","imdb_id"))
    val imageName = (json \ "movie_results" \ "poster_path").extract[String]
    s"$baseImageUrl$imageName"
  }
    .onFailure(logger.error(s"Unable to retrieve poster for $imdbId from TMDB",_:Throwable))
    .toOption

}

object TheMovieDB extends TheMovieDB(Config.tmdbApiKey) {}
