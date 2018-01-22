package me.gregd.cineworld.wiring

import stub.Stubs

trait StubConfigWiring extends ConfigWiring {
  def omdbConfig = Stubs.omdb.config
  def tmdbConfig = Stubs.tmdb.config
  def cineworldConfig = Stubs.cineworld.config
  def vueConfig = Stubs.vue.config
}
