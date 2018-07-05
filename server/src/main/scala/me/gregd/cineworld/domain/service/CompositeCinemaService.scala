package me.gregd.cineworld.domain.service

import me.gregd.cineworld.domain.model.Cinema
import me.gregd.cineworld.wiring.ChainConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompositeCinemaService(cineworld: CineworldService, vue: VueService, config: ChainConfig) {

  private def cinemasFor(chain: String) = chain match {
    case "vue"       => vue.retrieveCinemas()
    case "cineworld" => cineworld.retrieveCinemas()
    case _ => throw new IllegalArgumentException(s"$chain is not a valid cinema chain")
  }

  def getCinemas(): Future[Seq[Cinema]] = Future.traverse(config.enabled)(cinemasFor).map(_.flatten)

}
