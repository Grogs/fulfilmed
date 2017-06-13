package me.gregd.cineworld.util

import java.time._

import scala.concurrent.duration._


trait Clock {
  def today(): LocalDate
  def time(): LocalTime
  def now(): Timestamp
}

case class Timestamp(epoch: Long) {
  def age(implicit clock: Clock): FiniteDuration = (clock.now().epoch - epoch).millis
}


object RealClock extends Clock {
  def today(): LocalDate = LocalDate.now()

  def now(): Timestamp = Timestamp(System.currentTimeMillis())

  def time(): LocalTime = LocalTime.now(ZoneId.of("GB"))
}

case class FixedClock(date: LocalDate) extends Clock {
  def today(): LocalDate = date
  def time(): LocalTime = LocalTime.of(12,0,0)
  def now(): Timestamp = {
    val year = date.getYear
    val month = date.getMonthValue
    val day = date.getDayOfMonth
    val midnight = LocalDateTime.of(year, month, day, 0, 0)
    Timestamp(midnight.toEpochSecond(ZoneOffset.UTC))
  }
}

case class MutableClock(var current: LocalDateTime) extends Clock {
  def today(): LocalDate = current.toLocalDate
  def time(): LocalTime = current.toLocalTime
  def now(): Timestamp = Timestamp(current.toEpochSecond(ZoneOffset.UTC))
}
