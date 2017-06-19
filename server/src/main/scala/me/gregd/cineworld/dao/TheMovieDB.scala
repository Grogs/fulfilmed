package me.gregd.cineworld.dao

import javax.inject.{Inject, Singleton}

import com.typesafe.scalalogging.slf4j.LazyLogging
import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.values.{TmdbKey, TmdbRateLimit, TmdbUrl}
import me.gregd.cineworld.dao.model.{NowShowingResponse, TmdbMovie}
import me.gregd.cineworld.util.RateLimiter
import monix.execution.Scheduler
import org.json4s._
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scalacache.memoization._

@Singleton
class TheMovieDB @Inject()(apiKey: TmdbKey, ws: WSClient, url: TmdbUrl, cache: Cache, scheduler: Scheduler, rateLimit: TmdbRateLimit) extends LazyLogging {

  protected implicit val formats = DefaultFormats
  lazy private implicit val _ = cache.scalaCache

  private def key = apiKey.key

  lazy val limiter = RateLimiter(rateLimit.duration, rateLimit.amount)

  private def baseUrl = url.value
//  lazy val baseImageUrl = {
//    val json = get("configuration")
//    (json \ "images" \ "base_url").extract[String] + "w300"
//  }

  val baseImageUrl: String = "http://image.tmdb.org/t/p/w300"

  def fetchNowPlaying(): Future[Vector[TmdbMovie]] =
    Future.traverse(1 to 5)(fetchPage).map(_.flatten.toVector)

  def fetchImdbId(tmdbId: String): Future[Option[String]] = memoize(1.day){
    limiter{
      val url = s"$baseUrl/3/movie/$tmdbId?api_key=$key"
      def extractImdbId(res: WSResponse) = {
//        logger.info(s"Response for $tmdbId: ${res.body}")
        (res.json \ "imdb_id").asOpt[String]
      }
      ws.url(url).get().map(extractImdbId)
    }
  }

  private def fetchPage(page: Int): Future[Vector[TmdbMovie]] = memoize(1.day){
    limiter {
      val url = s"$baseUrl/3/movie/now_playing?api_key=$key&language=en-US&page=$page&region=GB"
      logger.info(s"Fetching now playing page $page")
      ws.url(url)
        .get()
        .map(_.json.as[NowShowingResponse].results)
    }
  }

  def alternateTitles(tmdbId: String): Future[List[String]] = memoize(3.days){
    limiter{
      val url = s"$baseUrl/3/movie/$tmdbId/alternative_titles?api_key=$key"
      def extract(r: WSResponse): List[String] = (r.json \\ "title").map(_.as[String]).toList
      ws.url(url).get().map(extract).recover{
        case ex =>
          logger.error(s"Unable to retrieve alternate titles for $tmdbId from TMDB", ex)
          Nil
      }
    }
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
