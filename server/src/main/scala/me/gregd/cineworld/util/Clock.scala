package me.gregd.cineworld.util

import java.time.LocalDate

trait Clock {
  def today(): LocalDate
}

object RealClock extends Clock {
  def today(): LocalDate = LocalDate.now()
}

case class FixedClock(date: LocalDate) extends Clock {
  def today(): LocalDate = date
}
