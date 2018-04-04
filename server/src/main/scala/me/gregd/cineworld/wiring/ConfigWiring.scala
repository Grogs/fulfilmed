package me.gregd.cineworld.wiring
import me.gregd.cineworld.config._

trait ConfigWiring {
  def omdbConfig: OmdbConfig
  def tmdbConfig: TmdbConfig
  def cineworldConfig: CineworldConfig
  def vueConfig: VueConfig
  def postcodesIoConfig: PostcodesIoConfig
}
