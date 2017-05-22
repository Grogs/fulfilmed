package me.gregd.cineworld.dao.cinema.cineworld.raw

import javax.inject.Inject

import me.gregd.cineworld.Cache
import me.gregd.cineworld.dao.cinema.cineworld.raw.model._
import org.json4s._
import org.json4s.native.JsonMethods._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scalacache.memoization._

class CineworldRepository @Inject()(ws: WSClient, cache: Cache) {

  implicit val _ = cache.scalaCache

  private object StringToLong
      extends CustomSerializer[Long](format =>
        ({
          case JString(x) => x.toLong
        }, {
          case x: Long => JInt(x)
        }))

  private object StringToInt
      extends CustomSerializer[Int](format =>
        ({
          case JString(x) => x.toInt
        }, {
          case x: Long => JInt(x)
        }))

  private implicit val formats = DefaultFormats + StringToLong + StringToInt

  private def curlCinemas(): Future[String] = memoize(1.day) {
    ws.url("https://www.cineworld.co.uk/getSites?json=1&max=200")
      .get()
      .map(_.body)
  }

  private def curl7DayListings(cinema: String): Future[String] = memoize(6.hours) {
    val url = s"https://www.cineworld.co.uk/pgm-site?si=$cinema&max=365"
    ws.url(url)
      .get()
      .map(_.body)
  }

  def retrieveCinemas(): Future[Seq[CinemaResp]] = {
    curlCinemas().map(r => parse(r).children.map(_.extract[CinemaResp]))
  }

  def retrieve7DayListings(cinema: String): Future[Seq[MovieResp]] = {
    curl7DayListings(cinema).map(r => parse(r).children.map(_.extract[MovieResp]))
  }

}

