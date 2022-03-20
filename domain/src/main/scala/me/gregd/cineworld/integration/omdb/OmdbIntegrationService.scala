package me.gregd.cineworld.integration.omdb

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeError
import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.config.OmdbConfig
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try
import scalacache.Cache
import scalacache.memoization._

class OmdbIntegrationService(ws: WSClient, implicit val cache: Cache[IO, String, String], config: OmdbConfig) extends LazyLogging with OmdbService {

  private implicit val formats = Json.reads[OmdbRatings]

  override def fetchRatings(imdbId: String): IO[RatingsResult] = {
    for {
      body <- curlFromRemote(imdbId)
      json = Json.parse(body)
    } yield
      RatingsResult(
        imdbRating(json),
        imdbVotes(json),
        metascore(json),
        rottenTomatoes(json)
      )
  }.recover {
    case ex =>
      logger.error(s"Failed to receive ratings for $imdbId", ex)
      RatingsResult(None, None, None, None)
  }

  private def curlFromRemote(id: String): IO[String] = memoizeF(Some(1.day)) {
    IO.fromFuture(IO(
      ws.url(s"${config.baseUrl}/?i=$id&apikey=${config.apiKey}")
        .get()
        .map(_.body)
    ))
  }

  private def imdbRating(json: JsValue) = (json \ "imdbRating").asOpt[String].flatMap(s => Try(s.toDouble).toOption)

  private def imdbVotes(json: JsValue) = (json \ "imdbVotes").asOpt[String].flatMap(s => Try(s.replaceAll(",", "").toInt).toOption)

  private def metascore(json: JsValue) = (json \ "Metascore").asOpt[String].flatMap(s => Try(s.toInt).toOption)

  private def rottenTomatoes(json: JsValue) =
    for {
      ratings <- (json \ "Ratings").asOpt[List[OmdbRatings]]
      rt      <- ratings.find(_.Source == "Rotten Tomatoes").map(_.Value)
    } yield rt

  case class OmdbRatings(Source: String, Value: String)
}
