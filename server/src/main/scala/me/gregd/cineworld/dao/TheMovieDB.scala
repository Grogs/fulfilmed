package me.gregd.cineworld.dao

import javax.inject.{Inject,Singleton}

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.TmdbConfig
import me.gregd.cineworld.dao.model.{ImdbIdAndAltTitles, NowShowingResponse, TmdbMovie, TmdbTitle}
import me.gregd.cineworld.util.RateLimiter
import monix.execution.Scheduler
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scalacache.memoization._

@Singleton
class TheMovieDB @Inject()(ws: WSClient, cache: Cache, scheduler: Scheduler, config: TmdbConfig) extends LazyLogging {

  private lazy implicit val _ = cache.scalaCache

  private def key = config.apiKey

  private lazy val limiter = RateLimiter(config.rateLimit.duration, config.rateLimit.count.value)

  private def baseUrl = config.baseUrl

  val baseImageUrl: String = "http://image.tmdb.org/t/p/w300"

  def fetchMovies(): Future[Vector[TmdbMovie]] =
    Future.sequence(Seq(fetchNowPlaying(), fetchUpcoming())).map(_.flatten.toVector)

  def fetchNowPlaying(): Future[Seq[TmdbMovie]] =
    Future.traverse(1 to 6)(fetchNowPlayingPage).map(_.flatten)

  def fetchUpcoming(): Future[Seq[TmdbMovie]] =
    Future.traverse(1 to 2)(fetchUpcomingPage).map(_.flatten)

  def fetchImdbId(tmdbId: String): Future[Option[String]] = {
    fetchImdbIdAndAlternateTitles(tmdbId).map(_.imdbId)
  }

  def alternateTitles(tmdbId: String): Future[List[String]] = {
    fetchImdbIdAndAlternateTitles(tmdbId).map(_.alternateTitles)
  }

  implicit val tmdbTitleFormat = Json.format[TmdbTitle]

  def extractAlternateTitles(r: JsValue) = (r \ "alternative_titles" \ "titles").as[Seq[TmdbTitle]].collect{
    case TmdbTitle("GB" | "US", title) => title
  }.toList

  private def fetchImdbIdAndAlternateTitles(tmdbId: String): Future[ImdbIdAndAltTitles] = memoize(1.day) {
    def extractImdbId(res: JsValue) = (res \ "imdb_id").asOpt[String]
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

  private def fetchNowPlayingPage(page: Int): Future[Vector[TmdbMovie]] = memoize(1.day) {
    limiter {
      val url = s"$baseUrl/3/movie/now_playing?api_key=$key&language=en-US&page=$page&region=GB"
      logger.info(s"Fetching now playing page $page")
      ws.url(url)
        .get()
        .map(_.json.as[NowShowingResponse].results)
    }
  }
  private def fetchUpcomingPage(page: Int): Future[Vector[TmdbMovie]] = memoize(1.day) {
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

  case class TmdbTitle(iso_3166_1: String, title: String)

  case class DateRange(maximum: String, minimum: String)

  case class NowShowingResponse(page: Double, results: Vector[TmdbMovie], dates: DateRange, total_pages: Double, total_results: Double)

  object TmdbMovie {
    implicit val movieFormat = Json.format[model.TmdbMovie]
  }

  object TmdbTitle {
    implicit val tmdbTitleFormat = Json.format[TmdbTitle]
  }

  object DateRange {
    implicit val dateRangeFormat = Json.format[model.DateRange]
  }

  object NowShowingResponse {
    implicit val nowShowingRespFormat: OFormat[NowShowingResponse] = Json.format[model.NowShowingResponse]
  }

}
