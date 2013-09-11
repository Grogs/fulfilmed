package me.gregd.cineworld

import me.gregd.cineworld.rest.CinemaService
import me.gregd.cineworld.dao.cineworld.Cineworld
import me.gregd.cineworld.dao.imdb.IMDb
import me.gregd.cineworld.util.TaskSupport
import me.gregd.cineworld.util.TaskSupport.TimeDSL
import grizzled.slf4j.Logging
import com.google.common.cache.{LoadingCache, CacheLoader, CacheBuilder}
import java.util.concurrent.TimeUnit._
import me.gregd.cineworld.domain.Movie

/**
 * Author: Greg Dorrell
 * Date: 09/06/2013
 */
object Config extends TaskSupport with Logging {

  val apiKey = "***REMOVED***"
  val rottenTomatoesApiKey = "***REMOVED***"

  val imdb = new IMDb(rottenTomatoesApiKey)
  val cineworld = new Cineworld(apiKey, imdb)
  val webservice = new CinemaService(cineworld)



//  schedule (
//    task = logger.info("test"),
//    frequency = 20 seconds
//  )

}
