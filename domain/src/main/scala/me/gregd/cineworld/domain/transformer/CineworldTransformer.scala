package me.gregd.cineworld.domain.transformer

import me.gregd.cineworld.domain.model.{Cinema, Coordinates, Film, Performance}
import me.gregd.cineworld.integration.cineworld.{CinemaResp, RawEvent, RawFilm}

object CineworldTransformer {

  def toCinema(cinemaResp: CinemaResp, coordinates: Option[Coordinates]): Cinema =
    Cinema(cinemaResp.id.toString, "Cineworld", cinemaResp.displayName, coordinates)

  def toFilm(raw: RawFilm): Film =
    Film(raw.id, raw.name, raw.posterLink)

  def toPerformances(raw: RawEvent): Performance = {
    val typ        = if (raw.attributeIds contains "3d") "3D" else "2D"
    val bookingUrl = raw.bookingLink
    val time       = raw.eventDateTime.replaceFirst(".*T", "").replaceFirst(":00$", "")
    Performance(time, !raw.soldOut, typ, bookingUrl, Option(raw.businessDay))
  }
}
