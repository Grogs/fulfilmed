package me.gregd.cineworld.domain

trait CinemaApi {

  def getMoviesAndPerformances(cinemaId: String, date: String): Map[Movie, List[Performance]]

}
