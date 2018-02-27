package me.gregd.cineworld.frontend.styles

object FilmsStyle {

  private val prefix = "FilmsStyle"

  val filmListContainer = s"$prefix-filmListContainer "
  val container = s"$prefix-container "
  val attribution = s"$prefix-attribution "
  val header = s"$prefix-header "
  val menuGroup = s"$prefix-menuGroup "
  val select = s"$prefix-select "

  val label = s"$prefix-label "

  val filmCard = s"$prefix-filmCard "
  val filmTitle = s"$prefix-filmTitle "

  val longFilmTitle = s"$prefix-longFilmTitle $filmTitle"

  val filmPosition = s"$prefix-filmPosition "
  val filmInfo = s"$prefix-filmInfo $filmPosition"
  val threedee = s"$prefix-threedee"
  val filmBackground = s"$prefix-filmBackground $filmPosition"
  val ratings = s"$prefix-ratings "
  val rating = s"$prefix-rating "
  val tmdb = s"$prefix-tmdb $rating"
  val rtAudience = s"$prefix-rtAudience $rating"
  val imdb = s"$prefix-imdb $rating"
  val times = s"$prefix-times "
  val time = s"$prefix-time "

}
