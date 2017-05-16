import java.nio.file.Files
import javax.inject.{Named => named}

import com.google.inject.{AbstractModule, Provides => provides}
import com.typesafe.config.ConfigFactory
import me.gregd.cineworld.Cache
import me.gregd.cineworld.dao.movies.RatingsCache
import me.gregd.cineworld.util.{Clock, FileCache, RealClock}
import monix.execution.Scheduler

import scalacache.ScalaCache

class Module extends AbstractModule {

  val prop = {
    val config = ConfigFactory.load
    config.getString _
  }

  @provides@named("rotten-tomatoes.api-key") def rottenTomatoesApiKey = prop("rotten-tomatoes.api-key")
  @provides@named("themoviedb.api-key") def tmdbApiKey = prop("themoviedb.api-key")

  val scalaCache = Cache(ScalaCache(new FileCache(Files.createTempDirectory("fulfilmed-cache").toString)))

  val ratingsCache = {
//    val db = DBMaker.fileDB("ratingsCache.mapdb").transactionEnable().make()
//    val map = db
//      .hashMap("ratings-cache", Serializer.STRING, Serializer.JAVA.asInstanceOf[Serializer[(Double, Int)]])
//      .expireAfterCreate(1.day.toMillis)
//      .expireAfterUpdate(1.day.toMillis)
//      .createOrOpen()
//    new RatingsCache(map.asScala, db.commit())
    new RatingsCache(collection.mutable.Map(), ())
  }

  override def configure(): Unit = {
    bind(classOf[Scheduler]).toInstance(monix.execution.Scheduler.global)
    bind(classOf[Clock]).toInstance(RealClock)
    bind(classOf[RatingsCache]).toInstance(ratingsCache)
    bind(classOf[Cache]).toInstance(scalaCache)
  }
}