package me.gregd.cineworld.dao.cinema.cineworld.raw

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import me.gregd.cineworld.dao.cinema.cineworld.raw.model._
import me.gregd.cineworld.domain._

object CineworldRepositoryTransformer {

  def toCinema(cinemaResp: CinemaResp): Cinema =
    Cinema(cinemaResp.id.toString, "Cineworld", cinemaResp.n, Option(Coordinates(cinemaResp.lat, cinemaResp.long)))

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
