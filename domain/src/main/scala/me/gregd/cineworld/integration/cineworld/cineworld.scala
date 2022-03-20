package me.gregd.cineworld.integration

package object cineworld {
  private val Postcode = ".*, ([A-Z]{1,2}[0-9][A-Z0-9]? [0-9][A-Z]{2})(?:, .*)?$".r
  case class CinemaResp(id: String, displayName: String, address: String) {
    val postcode: String = {
      val Postcode(res) = address
      res
    }
  }

  case class RawEvent(id: String, filmId: String, cinemaId: String, businessDay: String, eventDateTime: String, attributeIds: List[String], bookingLink: String, soldOut: Boolean)
  case class RawFilm(id: String, name: String, posterLink: String, link: String, attributeIds: List[String])
  case class ListingsBody(films: List[RawFilm], events: List[RawEvent])
}
