package me.gregd.cineworld.wiring
import me.gregd.cineworld.config.{CineworldConfig, OmdbConfig, TmdbConfig, VueConfig}

trait ConfigWiring {
  def omdbConfig: OmdbConfig
  def tmdbConfig: TmdbConfig
  def cineworldConfig: CineworldConfig
  def vueConfig: VueConfig
}
