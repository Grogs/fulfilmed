package me.gregd.cineworld.dao

import javax.inject.{Inject, Singleton, Named => named}

import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.model.{NowShowingResponse, TmdbMovie}
import me.gregd.cineworld.domain.Movie
import me.gregd.cineworld.util.Implicits._
import org.json4s._
import org.json4s.native.JsonMethods._
import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scalaj.http.HttpOptions.{connTimeout, readTimeout}
import scalaj.http.{Http, HttpRequest}

@Singleton
class TheMovieDB @Inject()(@named("themoviedb.api-key") apiKey: String, ws: WSClient) extends Logging {

  protected implicit val formats = DefaultFormats

  val baseUrl = "http://api.themoviedb.org/3"
//  lazy val baseImageUrl = {
//    val json = get("configuration")
//    (json \ "images" \ "base_url").extract[String] + "w300"
//  }

  val baseImageUrl: String = "http://image.tmdb.org/t/p/w300"

  protected def get(path: String, transform: HttpRequest => HttpRequest = m => m): JValue = {
    val req = transform(
      Http(s"$baseUrl/$path")
        .option(connTimeout(30000))
        .option(readTimeout(30000))
        .header("Accept", "application/json")
        .param("api_key", apiKey)
    )
    logger.debug(s"Getting: ${req.url} with params ${req.params}")
    val resp = req.asString
    parse(StringInput(resp.body))
  }

  def fetchNowPlaying(): Future[Seq[TmdbMovie]] = {
    Future.traverse(1 to 5)(fetchPage).map(_.flatten)
  }

  def fetchImdbId(tmdbId: String): Future[Option[String]] = {
    val url = s"$baseUrl/movie/$tmdbId?api_key=$apiKey"
    def extractImdbId(res: WSResponse) = (res.json \ "imdb_id").asOpt[String] match {
      case Some(r) => Some(r)
      case None =>
        println(res.json.toString)
        None
    }
    ws.url(url).get().map(extractImdbId)
  }

  private def fetchPage(page: Int): Future[Seq[TmdbMovie]] = {
    val url = s"$baseUrl/movie/now_playing?api_key=$apiKey&language=en-US&page=$page&region=GB"
    logger.info(s"Fetching now playing page $page")
    ws.url(url)
      .get()
      .map(_.json.as[NowShowingResponse].results)
  }

  private def fetchAlternateTitles(tmdbId: String): Seq[String] =
    Try {
      val json = get(s"movie/$tmdbId/alternative_titles")
      val titles = json \ "titles" \ "title"
      titles
        .extractOrElse[Seq[String]](
          Seq(titles.extract[String])
        )
    }.onFailure(logger.error(s"Unable to retrieve alternate titles for $tmdbId from TMDB", _: Throwable))
      .getOrElse(Nil)

  def alternateTitles(m: Movie): Seq[String] = {
    m.tmdbId.map(fetchAlternateTitles) getOrElse Nil
  }

}

package model {

  import play.api.libs.json.OFormat

  case class TmdbMovie(poster_path: Option[String],
                       adult: Boolean,
                       overview: String,
                       release_date: String,
                       genre_ids: List[Double],
                       id: Long,
                       original_title: String,
                       original_language: String,
                       title: String,
                       backdrop_path: Option[String],
                       popularity: Double,
                       vote_count: Double,
                       video: Boolean,
                       vote_average: Double)

  case class DateRange(maximum: String, minimum: String)

  case class NowShowingResponse(page: Double, results: List[TmdbMovie], dates: DateRange, total_pages: Double, total_results: Double)

  object TmdbMovie {
    implicit val movieFormat = Json.format[model.TmdbMovie]
  }

  object DateRange {
    implicit val dateRangeFormat = Json.format[model.DateRange]
  }

  object NowShowingResponse {
    implicit val nowShowingRespFormat: OFormat[NowShowingResponse] = Json.format[model.NowShowingResponse]
  }

}
