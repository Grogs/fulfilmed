package me.gregd.cineworld.util.caching

import scala.slick.lifted.{TableQuery, Tag}
import scala.slick.driver.H2Driver.simple._

class DatabaseCache[T](
  val cacheName: String,
  val db: Database,
  val deserialise: Array[Byte] => T,
  val serialise: T => Array[Byte]
) {
  import DatabaseCache.cacheEntries

  /**
   * Hook for subclasses/traits to provide additional criteria by which a cache entry may be invalid
   */
  def predicate(e: CacheEntry): Boolean = true

  def get(key: String): Option[T] = db withSession { implicit session =>
    val row = cacheEntries.filter( e =>
      e.cache === cacheName &&
        e.key === key
    ).take(1).firstOption.filter(predicate)
    row map (_.data) map deserialise
  }

  def put(key:String)(value: => T): Unit = db withSession { implicit session =>
    cacheEntries insert CacheEntry(cacheName,key,serialise(value))
  }

}

object DatabaseCache {
  val cacheEntries = TableQuery[CacheEntries]
  val ddl = cacheEntries.ddl
  def createIn(db:Database) = db withSession (ddl create _)
}

class CacheEntries(tag: Tag) extends Table[CacheEntry](tag, "CACHE_ENTRIES") {
  def cache = column[String]("CACHE_NAME")
  def key = column[String]("KEY")
  def data = column[Array[Byte]]("DATA")
  def timestamp = column[Long]("UPDATED")
  def * = (cache,key,data,timestamp) <> (CacheEntry.tupled, CacheEntry.unapply)
}

case class CacheEntry(name:String, key:String, data:Array[Byte], timestamp:Long = System.currentTimeMillis)

