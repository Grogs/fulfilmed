package me.gregd.cineworld.wiring

import me.gregd.cineworld.config.{MoviesConfig, PostcodesIoConfig}
import stub.Stubs
import scala.concurrent.duration._

trait StubConfigWiring extends ConfigWiring {
  def omdbConfig = Stubs.omdb.config
  def tmdbConfig = Stubs.tmdb.config
  def cineworldConfig = Stubs.cineworld.config
  def vueConfig = Stubs.vue.config
  def postcodesIoConfig = Stubs.postcodesio.config
  def moviesConfig = MoviesConfig(1.second)
}
