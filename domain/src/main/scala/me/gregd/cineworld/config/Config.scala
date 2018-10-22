package me.gregd.cineworld.config

import eu.timepit.refined.pureconfig.refTypeConfigConvert
import pureconfig.error.ConfigReaderFailures

case class Config(omdb: OmdbConfig,
                  tmdb: TmdbConfig,
                  cineworld: CineworldConfig,
                  vue: VueConfig,
                  postcodesIo: PostcodesIoConfig,
                  movies: MoviesConfig,
                  database: DatabaseConfig,
                  chains: ChainConfig)

object Config {
  def load(): Either[ConfigReaderFailures, Config] = pureconfig.loadConfig[Config]
}
