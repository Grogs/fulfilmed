package me.gregd.cineworld.config

import eu.timepit.refined.pureconfig.refTypeConfigConvert
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._
import pureconfig.generic._
import pureconfig._

case class Config(omdb: OmdbConfig,
                  tmdb: TmdbConfig,
                  cineworld: CineworldConfig,
                  vue: VueConfig,
                  postcodesIo: PostcodesIoConfig,
                  movies: MoviesConfig,
                  database: DatabaseConfig,
                  chains: ChainConfig)

object Config {
  def load(): Either[ConfigReaderFailures, Config] = ConfigSource.default.load[Config]
}
