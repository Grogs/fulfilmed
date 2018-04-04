package me.gregd.cineworld.dao.cinema.cineworld.raw

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import me.gregd.cineworld.dao.cinema.cineworld.raw.model._
import me.gregd.cineworld.domain._

object CineworldRepositoryTransformer {

  def toCinema(cinemaResp: CinemaResp, coordinates: Option[Coordinates]): Cinema =
    Cinema(cinemaResp.id.toString, "Cineworld", cinemaResp.displayName, coordinates)

  def toFilm(raw: RawFilm): Film = {
    val img = s"https://www.cineworld.co.uk/${raw.posterLink}"
    Film(raw.id, raw.name, img)
  }

  def toPerformances(raw: RawEvent): Performance = {
    val typ = if (raw.attributeIds contains "3d") "3D" else "2D"
    val bookingUrl = s"https://www.cineworld.co.uk/${raw.bookingLink}"
    val time = raw.eventDateTime.replaceFirst(".*T", "").replaceFirst(":00$", "")
    Performance(time, !raw.soldOut, typ, bookingUrl, Option(raw.businessDay))
  }
}
