package me.gregd.cineworld.domain.model

import java.time.LocalDate

case class Listings(cinemaId: String, date: LocalDate, listings: Seq[MovieListing])
