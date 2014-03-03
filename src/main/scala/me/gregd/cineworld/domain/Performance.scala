package me.gregd.cineworld.domain

import org.joda.time.DateTime

/**
 * Playing about with dates and times, always fun.
 * All Cineworld cinemas are in the same time zone, so afaik I don't have to worry about it.
 * I'll treat is as LocalDate (on US server) but not doing any tricky time stuff or getting times from any other sources.
 */
case class Performance (
  movie:Movie,
  dateandtime: DateTime
  //subtitled:Boolean = False
  //audio_described:Boolean = False
) {
  val date = dateandtime.toLocalDate
  val time = dateandtime.toLocalTime
}
