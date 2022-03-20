package me.gregd.cineworld.integration.tmdb

import cats.effect.IO
import cats.implicits.{catsSyntaxParallelTraverse1, catsSyntaxTuple2Parallel}
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.config.TmdbConfig
import org.typelevel.log4cats.slf4j.Slf4jLogger
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}
import scalacache.Cache
import scalacache.memoization._
import upperbound.Limiter
import upperbound.syntax.rate.rateOps

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class TmdbIntegrationService(ws: WSClient,
                             implicit val tmdbMovieCache: Cache[IO, String, Vector[TmdbMovie]],
                             implicit val alternateTitleCache: Cache[IO, String, ImdbIdAndAltTitles],
                             config: TmdbConfig) extends TmdbService {
  private val logger = Slf4jLogger.getLogger[IO]

  private lazy val key = config.apiKey

  private lazy val rateLimiter =
    Limiter.start[IO](config.rateLimit.count.value every config.rateLimit.duration).allocated.unsafeRunSync()(cats.effect.unsafe.IORuntime.global)._1 //todo resource leak

  private lazy val baseUrl = config.baseUrl

  override val baseImageUrl: String = "http://image.tmdb.org/t/p/w300"

  override def fetchMovies(): IO[Vector[TmdbMovie]] =
    (fetchNowPlaying(), fetchUpcoming()).parMapN(_ ++ _)

  override def fetchImdbId(tmdbId: String): IO[Option[String]] =
    fetchImdbIdAndAlternateTitles(tmdbId).map(_.imdbId)

  override def alternateTitles(tmdbId: String): IO[Vector[String]] =
    fetchImdbIdAndAlternateTitles(tmdbId).map(_.alternateTitles)

  private def fetchNowPlaying(): IO[Vector[TmdbMovie]] =
    (1 to 6).toList.parTraverse(fetchNowPlayingPage).map(_.flatten.toVector)

  private def fetchUpcoming(): IO[Vector[TmdbMovie]] =
    (1 to 2).toList.parTraverse(fetchUpcomingPage).map(_.flatten.toVector)

  private def fetchImdbIdAndAlternateTitles(tmdbId: String): IO[ImdbIdAndAltTitles] = {

    def extractImdbId(res: JsValue) = (res \ "imdb_id").asOpt[String]

    def extractAlternateTitles(r: JsValue) =
      (r \ "alternative_titles" \ "titles")
        .as[Vector[TmdbTitle]]
        .collect {
          case TmdbTitle("GB" | "US", title) => title
        }

    curlMovieAndAlternateTitles(tmdbId).map { res =>
      val json = res.json
      ImdbIdAndAltTitles(
        extractImdbId(json),
        extractAlternateTitles(json)
      )
    }
  }

  private def curlMovieAndAlternateTitles(tmdbId: String): IO[WSResponse] =
    rateLimiter.submit {
      IO.fromFuture(IO {
        val url = s"$baseUrl/3/movie/$tmdbId?append_to_response=alternative_titles&api_key=$key"
        ws.url(url).get()
      })
    }

  private def fetchNowPlayingPage(page: Int): IO[Vector[TmdbMovie]] = {
    rateLimiter.submit {
      val url = s"$baseUrl/3/movie/now_playing?api_key=$key&language=en-US&page=$page&region=GB"
      logger.info(s"Fetching now playing page $page") >>
        IO.fromFuture(
          IO(
            ws.url(url)
              .get()
              .map(_.json.as[NowShowingResponse].results)
          )
        )
    }
  }
  private def fetchUpcomingPage(page: Int): IO[Vector[TmdbMovie]] = {
    rateLimiter.submit {
      val url = s"$baseUrl/3/movie/now_playing?api_key=$key&language=en-US&page=$page&region=GB"
      logger.info(s"Fetching upcoming movies page $page") >>
        IO.fromFuture(
          IO(
            ws.url(url)
              .get()
              .map(_.json.as[NowShowingResponse].results)
          )
        )
    }
  }
}
