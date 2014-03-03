package me.gregd.cineworld.util

import me.gregd.cineworld.dao.cineworld.Cineworld

object FilmsToWatch extends App {

  val localCinema = Cineworld.getCinemas.find(
    _.name contains "West India Quay"
  ).get
  val films = Cineworld.getMovies(localCinema.id)

  films.filter(_.audienceRating.filter(_>60).isDefined) sortBy (_.audienceRating) foreach println

}
