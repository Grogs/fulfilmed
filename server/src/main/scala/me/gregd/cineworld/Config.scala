package me.gregd.cineworld

import javax.inject.{Named=>named}

import com.google.inject.{Provides=>provides, AbstractModule}
import me.gregd.cineworld.rest.CinemaService
import me.gregd.cineworld.dao.cineworld.Cineworld
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.util.TaskSupport
import me.gregd.cineworld.util.TaskSupport.TimeDSL
import grizzled.slf4j.Logging
import com.typesafe.config.ConfigFactory
import me.gregd.cineworld.dao.TheMovieDB
import scala.slick.driver.H2Driver.simple.Database
import me.gregd.cineworld.util.caching.DatabaseCache
import me.gregd.cineworld.domain.Movie
import scala.util.Try
import org.joda.time.LocalDate


class Config extends AbstractModule with TaskSupport with Logging {
  val prop = ConfigFactory.load.getString _
  
  @provides@named("cineworld.api-key") def apiKey = prop("cineworld.api-key")
  @provides@named("rotten-tomatoes.api-key") def rottenTomatoesApiKey = prop("rotten-tomatoes.api-key")
  @provides@named("themoviedb.api-key") def tmdbApiKey = prop("themoviedb.api-key")
  val dbUrl = Try(prop("database.caching")) getOrElse "jdbc:h2:mem:caching;DB_CLOSE_DELAY=-1"
  logger.info(s"Using the following url for the caching DB:\n$dbUrl")

  lazy val cacheDB = {
    val db = Database forURL dbUrl
    DatabaseCache createIn db
    db
  }
//  lazy val moviesCache = new DatabaseCache[Seq[Movie]]("movies",cacheDB,new String(_:Array[Byte], "UTF-8").unpickle[Seq[Movie]],_.pickle.value.getBytes)

//  lazy val tmdb = new TheMovieDB(tmdbApiKey)
//  lazy val imdb = new Movies(rottenTomatoesApiKey, tmdb)
//  lazy val cineworld = new Cineworld(apiKey, imdb, tmdb)
//  lazy val webservice = new CinemaService(cineworld)

  //TODO refresh
//  schedule(
//    task = {
//      val today = new LocalDate
//      cineworld.movieCache.refresh("66", today)
//      cineworld.performanceCache.refresh("66", today)
//      val tomorrow = today plusDays 1
//      cineworld.movieCache.refresh("66", tomorrow)
//      cineworld.performanceCache.refresh("66", tomorrow)
//      cineworld.refreshCinemaCity()
//    },
//    frequency = 1.hour,
//    delay = 5.minutes
//  )
  override def configure() = {}
}

object Config extends Config