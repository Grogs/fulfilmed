package me.gregd.cineworld.frontend

import me.gregd.cineworld.domain.{Movie, Performance}

case class FilmsState(isLoading: Boolean, films: Map[Movie, List[Performance]])
