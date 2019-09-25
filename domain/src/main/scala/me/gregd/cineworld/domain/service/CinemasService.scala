package me.gregd.cineworld.domain.service

import me.gregd.cineworld.config.ChainConfig
import me.gregd.cineworld.domain.model.Cinema
import monix.eval.Task

class CinemasService(cineworld: CineworldService, vue: VueService, config: ChainConfig) extends Cinemas[Task] {

  private def cinemasFor(chain: String) = chain match {
    case "vue"       => vue.retrieveCinemas()
    case "cineworld" => cineworld.retrieveCinemas()
    case _ => throw new IllegalArgumentException(s"$chain is not a valid cinema chain")
  }

  def getCinemas(): Task[Seq[Cinema]] = Task.traverse(config.enabled)(chain => Task.deferFuture(cinemasFor(chain))).map(_.flatten)

}
