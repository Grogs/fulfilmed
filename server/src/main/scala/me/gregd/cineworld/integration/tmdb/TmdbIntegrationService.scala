package me.gregd.cineworld.integration.tmdb

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.integration.tmdb.model._
import me.gregd.cineworld.util.RateLimiter
import me.gregd.cineworld.wiring.TmdbConfig
import monix.execution.Scheduler
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}
import scalacache.ScalaCache
import scalacache.memoization._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class TmdbIntegrationService(ws: WSClient, implicit val cache: ScalaCache[Array[Byte]], scheduler: Scheduler, config: TmdbConfig) extends LazyLogging {

  private lazy val key = config.apiKey

  private lazy val limiter = RateLimiter(config.rateLimit.duration, config.rateLimit.count.value)

  private lazy val baseUrl = config.baseUrl

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

  private def fetchImdbIdAndAlternateTitles(tmdbId: String): Future[ImdbIdAndAltTitles] = memoize(1.day) {

    def extractImdbId(res: JsValue) = (res \ "imdb_id").asOpt[String]

    def extractAlternateTitles(r: JsValue) =
      (r \ "alternative_titles" \ "titles")
        .as[Seq[TmdbTitle]]
        .collect {
          case TmdbTitle("GB" | "US", title) => title
        }
        .toList

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
      logger.info(s"Fetching upcoming movies page $page")
      ws.url(url)
        .get()
        .map(_.json.as[NowShowingResponse].results)
    }
  }
}
