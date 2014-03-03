package me.gregd.cineworld.util.caching

import scala.slick.session.{PositionedResult, Database}
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import java.util.Date
import java.sql.{Blob, Timestamp}

abstract class DatabaseCache[T] {

  val cacheName: String
  val db: Database
  val deserialise: Array[Byte] => T
  val serialise: T => Array[Byte]

  implicit val mapper = GetResult[CacheRow]( rs =>
    CacheRow( rs.<<, rs.<<, rs.nextBytes(), rs.<< )
  )
  implicit val GetByteArr = GetResult(r => r.nextBytes)

  //TODO

  def get(key: String): Option[T] = db withSession { implicit session =>
    val row = sql"""
        select cache_name, key, value, updated_ts
        from test.cache
        where cache_name = '$cacheName'
          and key = '$key'
      """.as[CacheRow]
    row.firstOption map (_.value) map deserialise
  }

  def put(key:String)(value: => T): Unit = db withSession {
//    sqlu"""
//      insert into test.cache values ($cacheName, $key, ${serialise(value)}, ${new Date().getTime})
//    """.execute()
    ???
  }

  case class CacheRow(cache: String, key: String, value: Array[Byte], updated: Long)

}


