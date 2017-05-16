package me.gregd.cineworld.dao.movies

import javax.inject.Inject

import grizzled.slf4j
import org.json4s.DefaultFormats
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class Ratings @Inject()(ws: WSClient, cache: RatingsCache) extends slf4j.Logging {

  implicit val formats = DefaultFormats

  private def extract(json: JsValue): Try[(Double, Int)] = Try {
    val rating = (json \ "imdbRating").as[String].toDouble
    val votes = (json \ "imdbVotes").as[String].replaceAll(",", "").toInt
    rating -> votes
  }

  def ratingAndVotes(id: String): Future[Option[(Double, Int)]] = {
    def fetchFromRemoteAndCache = {
      val res = fetchFromRemote(id)
      res.filter(_.isDefined).map(_.get).foreach(cache.insert(id))
      res
    }

    fetchFromCache(id).map(Some.apply).recoverWith { case _ => fetchFromRemoteAndCache }
  }

  private def fetchFromCache(id: String): Future[(Double, Int)] = {
    cache.lookup(id) match {
      case Some(res) => Future.successful(res)
      case None => Future.failed(new NoSuchElementException(s"No rating for $id in cache"))
    }
  }

  protected def fetchFromRemote(id: String): Future[Option[(Double, Int)]] = {
    val res =
      ws.url(s"http://www.omdbapi.com/?i=$id")
        .get()
        .map { resp =>
          extract(resp.json) match {
            case Success(r) => Some(r)
            case Failure(ex) =>
              logger.info(s"Failed parse OMDB response for $id", ex)
              None
          }
        }
    res.onFailure { case ex => logger.error(s"Failed to retrieve IMDB rating for $id", ex) }
    res
  }
}
