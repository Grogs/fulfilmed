package me.gregd.cineworld.dao.cineworld

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import me.gregd.cineworld.domain.{Cinema, Film, Movie, Performance}
import org.json4s._
import org.json4s.native.JsonMethods._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

class CineworldRepository @Inject()(ws: WSClient) {

  object StringToLong
      extends CustomSerializer[Long](format =>
        ({
          case JString(x) => x.toLong
        }, {
          case x: Long => JInt(x)
        }))

  object StringToInt
      extends CustomSerializer[Int](format =>
        ({
          case JString(x) => x.toInt
        }, {
          case x: Long => JInt(x)
        }))

  implicit val formats = DefaultFormats + StringToLong + StringToInt
  implicit val ec = ExecutionContext.global

  def retrieveCinemas(): Future[Seq[CinemaResp]] =
    ws.url("https://www.cineworld.co.uk/getSites?json=1&max=200")
      .get()
      .map(r => parse(r.body).children.map(_.extract[CinemaResp]))

  def retrieve7DayListings(cinema: String): Future[Seq[MovieResp]] =
    ws.url(s"https://www.cineworld.co.uk/pgm-site?si=$cinema&max=365")
      .get()
      .map(r => parse(r.body).children.map(_.extract[MovieResp]))

}

object CineworldRepository {
  def toCinema(cinemaResp: CinemaResp): Cinema =
    Cinema(cinemaResp.id.toString, cinemaResp.n)

  def toMovie(cinemaId: String, movieResp: MovieResp): Map[Film, Map[LocalDate, Seq[Performance]]] =
    movieResp.TYPD.map { typ =>
      val showings = movieResp.BD.map(toPerformances(cinemaId)).toMap
      val film: Film = toFilm(movieResp)
      film -> showings
    }.toMap

  def toFilm(movieResp: MovieResp): Film = {
    val img = s"https://www.cineworld.co.uk/xmedia-cw/repo/feats/posters/${movieResp.code}.jpg"
    Film(movieResp.code, movieResp.n, img)
  }

  def toPerformances(cinemaId: String)(day: Day): (LocalDate, Seq[Performance]) = {
    val date = LocalDate.parse(day.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val showings = day.P.map { s =>
      val typ = if (s.is3d) "3D" else "2D"
      val bookingUrl = s"https://www.cineworld.co.uk/ecom-tickets?siteId=$cinemaId&prsntId=${s.code}"
      Performance(s.time, !s.sold, typ, bookingUrl, Option(day.date))
    }
    date -> showings
  }
}

case class CinemaResp(excode: Int, id: Long, addr: String, idx: Int, n: String, pn: String, long: Double, lat: Double, url: String)

case class MovieResp(dur: Int, BD: Seq[Day], code: String, rdesc: String, TYP: Seq[String], rfn: String, rid: Int, rn: String, rtn: String, n: String, TYPD: Seq[String])

case class Day(date: String, P: Seq[Showing], d: Long)

case class Showing(dt: Long, dub: Short, sub: Short, sold: Boolean, code: Int, vn: String, is3d: Boolean, dattr: String, time: String, attr: String, vt: Int)
