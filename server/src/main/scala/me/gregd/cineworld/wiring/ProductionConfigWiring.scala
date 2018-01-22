package me.gregd.cineworld.wiring

import me.gregd.cineworld.config.Config
import eu.timepit.refined.pureconfig.refTypeConfigConvert

trait ProductionConfigWiring {
  val appConfig = pureconfig.loadConfig[Config] match {
    case Left(failures) =>
      System.err.println(failures.toList.mkString("Failed to read config, errors:\n\t", "\n\t", ""))
      throw new IllegalArgumentException("Invalid config")
    case Right(conf) => conf
  }

  val omdbConfig = appConfig.omdb
  val tmdbConfig = appConfig.tmdb
  val cineworldConfig = appConfig.cineworld
  val vueConfig = appConfig.vue
}
