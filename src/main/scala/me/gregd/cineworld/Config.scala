package me.gregd.cineworld

import me.gregd.cineworld.rest.CinemaService
import me.gregd.cineworld.dao.cineworld.Cineworld
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.util.TaskSupport
import me.gregd.cineworld.util.TaskSupport.TimeDSL
import grizzled.slf4j.Logging
import com.typesafe.config.ConfigFactory

object Config extends TaskSupport with Logging {
  val prop = ConfigFactory.load.getString _
  
  val apiKey = prop("cineworld.api-key")
  val rottenTomatoesApiKey = prop("rotten-tomatoes.api-key")

  val imdb = new Movies(rottenTomatoesApiKey)
  val cineworld = new Cineworld(apiKey, imdb)
  val webservice = new CinemaService(cineworld)

  schedule(
    task = {
      cineworld.movieCache.refresh("66")
      cineworld.performanceCache.refresh("66")
    },
    frequency = 1 hour
  )

}
