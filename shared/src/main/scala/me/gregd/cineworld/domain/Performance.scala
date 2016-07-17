package me.gregd.cineworld.domain

import java.time.LocalDate

/**
 * Created by Greg Dorrell on 10/05/2014.
 */
case class Performance(
  time:String,
  available:Boolean,
  `type`:String,
  booking_url: String,
  date: Option[String] = None
) {
//  date.foreach( d => assert(d.matches("""\d{4}[-/]\d{2}[-/]\d{2}"""), "Invalid date"))
}
