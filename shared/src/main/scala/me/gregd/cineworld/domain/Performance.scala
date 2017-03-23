package me.gregd.cineworld.domain

case class Performance(
  time:String,
  available:Boolean,
  `type`:String,
  booking_url: String,
  date: Option[String] = None
)