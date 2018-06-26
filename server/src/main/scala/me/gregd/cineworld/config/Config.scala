package me.gregd.cineworld.config

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.Url
import eu.timepit.refined.pureconfig.refTypeConfigConvert
import pureconfig.error.ConfigReaderFailures

import scala.concurrent.duration.FiniteDuration

object Config {
  def load(): Either[ConfigReaderFailures, Config] = pureconfig.loadConfig[Config]}

case class Config(omdb: OmdbConfig, tmdb: TmdbConfig, cineworld: CineworldConfig, vue: VueConfig, postcodesIo: PostcodesIoConfig, movies: MoviesConfig)

case class OmdbConfig(baseUrl: String Refined Url, apiKey: String)
case class TmdbConfig(baseUrl: String Refined Url, apiKey: String, rateLimit: TmdbRateLimit)
case class CineworldConfig(baseUrl: String Refined Url)
case class VueConfig(baseUrl: String Refined Url)
case class PostcodesIoConfig(baseUrl: String Refined Url)
case class MoviesConfig(cacheTimeout: FiniteDuration)

case class TmdbRateLimit(duration: FiniteDuration, count: Int Refined Positive)
