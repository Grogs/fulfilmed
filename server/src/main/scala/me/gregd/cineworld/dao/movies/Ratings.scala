package me.gregd.cineworld.dao.movies

import javax.inject.Inject

import com.typesafe.scalalogging.slf4j.LazyLogging
import me.gregd.cineworld.Cache
import me.gregd.cineworld.config.values.OmdbKey
import org.json4s.DefaultFormats
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scalacache.memoization._

class Ratings @Inject()(ws: WSClient, cache: Cache, apiKey: OmdbKey) extends LazyLogging {

  lazy implicit val _ = cache.scalaCache

  implicit val formats = DefaultFormats

  private def extract(json: JsValue): Try[(Double, Int)] =
    Try {
      val rating = (json \ "imdbRating").as[String].toDouble
      val votes = (json \ "imdbVotes").as[String].replaceAll(",", "").toInt
      rating -> votes
    } match {
      case f@Failure(ex) =>
        logger.warn(s"Failed parse OMDB response: ${ex.getClass.getSimpleName}: ${ex.getMessage}")
        f
      case s@Success(_) => s
    }

  def ratingAndVotes(imdbId: String): Future[Option[(Double, Int)]] =
    for {
      body <- curlFromRemote(imdbId)
      json = Json.parse(body)
      extracted = extract(json)
    } yield extracted.toOption


  private def curlFromRemote(id: String): Future[String] = memoize(1.day) {
    ws.url(s"http://www.omdbapi.com/?i=$id&apikey=${apiKey.key}")
      .get()
      .map(_.body)
  }

}
