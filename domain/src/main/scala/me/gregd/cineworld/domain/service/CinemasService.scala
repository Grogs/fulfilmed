package me.gregd.cineworld.domain.service

import me.gregd.cineworld.config.ChainConfig
import me.gregd.cineworld.domain.model.Cinema
import cats.effect.IO
import cats.syntax.traverse._

import scala.concurrent.ExecutionContext

class CinemasService(cineworld: CineworldService, vue: VueService, config: ChainConfig) extends Cinemas {

  private def cinemasFor(chain: String) = chain match {
    case "vue"       => vue.retrieveCinemas()
    case "cineworld" => cineworld.retrieveCinemas()
    case _           => throw new IllegalArgumentException(s"$chain is not a valid cinema chain")
  }

  def getCinemas: IO[List[Cinema]] = config.enabled.toList.traverse(chain => cinemasFor(chain)).map(_.flatten)

}
