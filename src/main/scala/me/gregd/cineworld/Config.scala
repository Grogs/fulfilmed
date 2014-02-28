package me.gregd.cineworld

import me.gregd.cineworld.rest.CinemaService
import me.gregd.cineworld.dao.cineworld.Cineworld
import me.gregd.cineworld.dao.imdb.Ratings
import me.gregd.cineworld.util.TaskSupport
import me.gregd.cineworld.util.TaskSupport.TimeDSL
import grizzled.slf4j.Logging
import collection.JavaConverters._
import com.typesafe.config.ConfigFactory

/**
 * Author: Greg Dorrell
 * Date: 09/06/2013
 */
object Config extends TaskSupport with Logging {
  val prop = ConfigFactory.load.getString _
  
  val apiKey = prop("cineworld.api-key")
  val rottenTomatoesApiKey = prop("rotten-tomatoes.api-key")

  val imdb = new Ratings(rottenTomatoesApiKey)
  val cineworld = new Cineworld(apiKey, imdb)
  val webservice = new CinemaService(cineworld)

  schedule(
    task = {
      cineworld.movieCache.asMap foreach { a => Unit
//        db.
      }
    },
    frequency = 5 minutes
  )


//  schedule (
//    task = logger.info("test"),
//    frequency = 20 seconds
//  )

}
