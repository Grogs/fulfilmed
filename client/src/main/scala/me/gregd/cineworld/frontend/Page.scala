package me.gregd.cineworld.frontend

import me.gregd.cineworld.frontend.components.film.FilmPageComponent.Date


sealed trait Page
case object Home extends Page
case class Films(cinemaId: String, initialDate: Date) extends Page {
  def on(newDate: Date): Films = copy(initialDate = newDate)
}