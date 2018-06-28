package me.gregd.cineworld.domain.model

case class Performance(
  time:String,
  available:Boolean,
  `type`:String,
  booking_url: String,
  date: Option[String] = None
)