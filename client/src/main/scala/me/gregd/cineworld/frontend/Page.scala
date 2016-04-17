package me.gregd.cineworld.frontend

sealed trait Page
case object Home extends Page
case class Films(cinemaId: String) extends Page