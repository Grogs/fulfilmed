package me.gregd.cineworld.wiring

import me.gregd.cineworld.config.PostcodesIoConfig
import stub.Stubs

trait StubConfigWiring extends ConfigWiring {
  def omdbConfig = Stubs.omdb.config
  def tmdbConfig = Stubs.tmdb.config
  def cineworldConfig = Stubs.cineworld.config
  def vueConfig = Stubs.vue.config
  def postcodesIoConfig = Stubs.postcodesio.config
}
