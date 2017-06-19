package me.gregd.cineworld.config

import scala.concurrent.duration.FiniteDuration

package object values {
  case class CineworldUrl(value: String)
  case class VueUrl(value: String)
  case class TmdbUrl(value: String)
  case class OmdbUrl(value: String)
  case class TmdbKey(key: String)
  case class OmdbKey(key: String)
  case class TmdbRateLimit(duration: FiniteDuration, amount: Int)
}
