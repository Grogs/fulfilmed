package me.gregd.cineworld.dao.cineworld

import javax.inject.Inject

import me.gregd.cineworld.domain.{Cinema, Movie}
import org.json4s._
import org.json4s.native.JsonMethods._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

class CineworldDao @Inject() (ws: WSClient) {

  object StringToLong extends CustomSerializer[Long](format => ({ case JString(x) => x.toLong }, { case x: Long => JInt(x) }))
  object StringToInt extends CustomSerializer[Int](format => ({ case JString(x) => x.toInt }, { case x: Long => JInt(x) }))
  implicit val formats = DefaultFormats + StringToLong + StringToInt
  implicit val ec = ExecutionContext.global

  def retrieveCinemas(): Future[Seq[CinemaResp]] =
    ws.url("https://www.cineworld.co.uk/getSites?json=1&max=200")
      .get()
      .map(r => parse(r.body).children.map(_.extract[CinemaResp]))


  def retrieve7DayListings(cinema: String): Future[Seq[MovieResp]] =
    ws.url(s"https://www.cineworld.co.uk/pgm-site?si=$cinema&max=365")
      .get()
      .map( r => parse(r.body).children.map(_.extract[MovieResp]))

}

object CineworldDao {
  def toCinema(cinemaResp: CinemaResp): Cinema =
    Cinema(cinemaResp.id.toString, cinemaResp.n)

  def toMovie(movieResp: MovieResp): Seq[Movie] =
    movieResp.TYPD map { typ =>
      val img = s"https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters/${movieResp.code}.jpg"
      Movie(movieResp.n, Option(movieResp.code), Option(typ), None, None, None, None, None, Option(img))
    }
}

case class CinemaResp(excode: Int, id: Long, addr: String, idx: Int, n: String, pn: String,
                      long: Double, lat: Double, url: String)


case class MovieResp(dur: Int, BD: Seq[Day], code: String, rdesc: String, TYP: Seq[String],
                     rfn: String, rid: Int, rn: String, rtn: String, n: String, TYPD: Seq[String])

case class Day(date: String, P: Seq[Showing], d: Long)

case class Showing(dt: Long, dub: Short, sub: Short, sold: Boolean, code: Int, vn: String,
                   is3d: Boolean, dattr: String, time: String, attr: String, vt: Int)