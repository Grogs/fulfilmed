package me.gregd.cineworld.dao.movies

import java.text.NumberFormat
import javax.inject.Inject

import grizzled.slf4j
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.parse
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scalaj.http.Http
import scalaj.http.HttpOptions.{connTimeout, readTimeout}

class Ratings @Inject()(ws: WSClient) extends slf4j.Logging {

  implicit val formats = DefaultFormats

  protected[movies] def imdbRatingAndVotes(id: String): Option[(Double, Int)] = {
    logger.debug(s"Retreiving IMDb rating and votes for $id")
    val resp = Http(s"http://www.omdbapi.com/?i=$id")
      .options(connTimeout(30000), readTimeout(30000))
      .asString
      .body
    logger.debug(s"OMDb response for $id:\n$resp")
    val rating = Try(
      (parse(resp) \ "imdbRating").extract[String].toDouble
    ).toOption
    val votes = Try(
      (parse(resp) \ "imdbVotes").extract[String] match { //needed as ',' is used as decimal mark
        case s => NumberFormat.getIntegerInstance.parse(s).intValue
      }
    ).toOption
    logger.debug(s"$id: $rating with $votes votes")
    (rating, votes) match {
      case (Some(r), Some(v)) => Option(r, v)
      case _ => None
    }
  }

  private def extract(json: JsValue): (Double, Int) = {
    val rating = (json \ "imdbRating").as[String].toDouble
    val votes = (json \ "imdbVotes").as[String].replaceAll(",", "").toInt
    rating -> votes
  }

  def ratingAndVotes(id: String): Future[(Double, Int)] = {
    ws.url(s"http://www.omdbapi.com/?i=$id")
      .get()
      .map(resp => extract(resp.json))
  }

}
