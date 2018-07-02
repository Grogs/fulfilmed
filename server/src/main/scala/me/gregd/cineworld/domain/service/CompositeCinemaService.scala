package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.Cinema

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompositeCinemaService(cineworld: CineworldService, vue: VueService) {

  def getCinemas(): Future[List[Cinema]] = Future.sequence(List(cineworld.retrieveCinemas(), vue.retrieveCinemas())).map(_.flatten)

}
