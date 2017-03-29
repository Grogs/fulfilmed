package me.gregd.cineworld.util

import java.time.{LocalDate, LocalDateTime, ZoneOffset}

import scala.concurrent.duration._


trait Clock {
  def today(): LocalDate
  def now(): Timestamp
}

case class Timestamp(epoch: Long) {
  def age(implicit clock: Clock): FiniteDuration = (clock.now().epoch - epoch).millis
}


object RealClock extends Clock {
  def today(): LocalDate = LocalDate.now()

  def now(): Timestamp = Timestamp(System.currentTimeMillis())
}

case class FixedClock(date: LocalDate) extends Clock {
  def today(): LocalDate = date
  def now(): Timestamp = {
    val year = date.getYear
    val month = date.getMonthValue
    val day = date.getDayOfMonth
    val midnight = LocalDateTime.of(year, month, day, 0, 0)
    Timestamp(midnight.toEpochSecond(ZoneOffset.UTC))
  }
}

case class MutableClock(var time: LocalDateTime) extends Clock {
  def today(): LocalDate = time.toLocalDate

  def now(): Timestamp = Timestamp(time.toEpochSecond(ZoneOffset.UTC))
}
