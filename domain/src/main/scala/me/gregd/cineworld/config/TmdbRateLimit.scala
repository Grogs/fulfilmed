package me.gregd.cineworld.config
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive

import scala.concurrent.duration.FiniteDuration

case class TmdbRateLimit(duration: FiniteDuration, count: Int Refined Positive)
