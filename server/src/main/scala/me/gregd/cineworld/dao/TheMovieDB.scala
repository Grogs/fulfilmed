package me.gregd.cineworld.dao

import javax.inject.{Inject, Singleton}

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.values.{TmdbKey, TmdbRateLimit, TmdbUrl}
import me.gregd.cineworld.dao.model.{ImdbIdAndAltTitles, NowShowingResponse, TmdbMovie}
import me.gregd.cineworld.util.RateLimiter
import monix.execution.Scheduler
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scalacache.memoization._

@Singleton
class TheMovieDB @Inject()(apiKey: TmdbKey, ws: WSClient, url: TmdbUrl, cache: Cache, scheduler: Scheduler, rateLimit: TmdbRateLimit) extends LazyLogging {

  private lazy implicit val _ = cache.scalaCache

  private def key = apiKey.key

  private lazy val limiter = RateLimiter(rateLimit.duration, rateLimit.amount)

  private def baseUrl = url.value

  val baseImageUrl: String = "http://image.tmdb.org/t/p/w300"

  def fetchNowPlaying(): Future[Vector[TmdbMovie]] =
    Future.traverse(1 to 5)(fetchPage).map(_.flatten.toVector)

  def fetchImdbId(tmdbId: String): Future[Option[String]] = {
    fetchImdbIdAndAlternateTitles(tmdbId).map(_.imdbId)
  }

  def alternateTitles(tmdbId: String): Future[List[String]] = {
    fetchImdbIdAndAlternateTitles(tmdbId).map(_.alternateTitles)
  }

  private def fetchImdbIdAndAlternateTitles(tmdbId: String): Future[ImdbIdAndAltTitles] = memoize(1.day) {
    def extractImdbId(res: JsValue) = (res \ "imdb_id").asOpt[String]
    def extractAlternateTitles(r: JsValue) = (r \ "alternative_titles" \\ "title").map(_.as[String]).toList
    curlMovieAndAlternateTitles(tmdbId).map { res =>
      val json = res.json
      ImdbIdAndAltTitles(
        extractImdbId(json),
        extractAlternateTitles(json)
      )
    }
  }

  private def curlMovieAndAlternateTitles(tmdbId: String): Future[WSResponse] =
    limiter {
      val url = s"$baseUrl/3/movie/$tmdbId?append_to_response=alternative_titles&api_key=$key"
      ws.url(url).get()
    }

  private def fetchPage(page: Int): Future[Vector[TmdbMovie]] = memoize(1.day) {
    limiter {
      val url = s"$baseUrl/3/movie/now_playing?api_key=$key&language=en-US&page=$page&region=GB"
      logger.info(s"Fetching now playing page $page")
      ws.url(url)
        .get()
        .map(_.json.as[NowShowingResponse].results)
    }
  }
}

package model {

  case class ImdbIdAndAltTitles(imdbId: Option[String], alternateTitles: List[String])

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
