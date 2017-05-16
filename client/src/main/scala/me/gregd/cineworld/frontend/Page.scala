package me.gregd.cineworld.frontend

import me.gregd.cineworld.frontend.components.FilmPageComponent.model.Date


sealed trait Page
case object Home extends Page
case class Films(cinemaId: String, initialDate: Date) extends Page