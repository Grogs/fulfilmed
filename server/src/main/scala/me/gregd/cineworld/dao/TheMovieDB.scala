package me.gregd.cineworld.dao

import javax.inject.{Inject, Singleton, Named => named}

import grizzled.slf4j.Logging
import me.gregd.cineworld.config.values.{TmdbKey, TmdbUrl}
import me.gregd.cineworld.dao.model.{NowShowingResponse, TmdbMovie}
import org.json4s._
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TheMovieDB @Inject()(apiKey: TmdbKey, ws: WSClient, url: TmdbUrl) extends Logging {

  protected implicit val formats = DefaultFormats

  private val key = apiKey.key

  private val baseUrl = url.value
//  lazy val baseImageUrl = {
//    val json = get("configuration")
//    (json \ "images" \ "base_url").extract[String] + "w300"
//  }

  val baseImageUrl: String = "http://image.tmdb.org/t/p/w300"

  def fetchNowPlaying(): Future[Vector[TmdbMovie]] =
    Future.traverse(1 to 5)(fetchPage).map(_.flatten.toVector)

  def fetchImdbId(tmdbId: String): Future[Option[String]] = {
    val url = s"$baseUrl/3/movie/$tmdbId?api_key=$key"
    def extractImdbId(res: WSResponse) = (res.json \ "imdb_id").asOpt[String]
    ws.url(url).get().map(extractImdbId)
  }

  private def fetchPage(page: Int): Future[Vector[TmdbMovie]] = {
    val url = s"$baseUrl/3/movie/now_playing?api_key=$key&language=en-US&page=$page&region=GB"
    logger.info(s"Fetching now playing page $page")
    ws.url(url)
      .get()
      .map(_.json.as[NowShowingResponse].results)
  }

  private def fetchAlternateTitles(tmdbId: String): Future[List[String]] = {
    val url = s"$baseUrl/3/movie/$tmdbId/alternative_titles?api_key=$key"
    def extract(r: WSResponse): List[String] = (r.json \\ "title").map(_.as[String]).toList
    ws.url(url).get().map(extract).recover{
      case ex =>
        logger.error(s"Unable to retrieve alternate titles for $tmdbId from TMDB", ex)
        Nil
    }
  }

  def alternateTitles(tmdbId: String): Future[List[String]] = {
    fetchAlternateTitles(tmdbId)
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

  case class NowShowingResponse(page: Double, results: Vector[TmdbMovie], dates: DateRange, total_pages: Double, total_results: Double)

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
