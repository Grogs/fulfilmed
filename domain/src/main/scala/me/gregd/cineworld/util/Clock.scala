package me.gregd.cineworld.util

import java.time._

import scala.concurrent.duration._

trait Clock {
  def today(): LocalDate
  def time(): LocalTime
}

object RealClock extends Clock {
  def today(): LocalDate = LocalDate.now()
  def time(): LocalTime  = LocalTime.now(ZoneId.of("GB"))
}

case class FixedClock(date: LocalDate) extends Clock {
  def today(): LocalDate = date
  def time(): LocalTime  = LocalTime.of(12, 0, 0)
}

case class MutableClock(var current: LocalDateTime) extends Clock {
  def today(): LocalDate = current.toLocalDate
  def time(): LocalTime  = current.toLocalTime
}
