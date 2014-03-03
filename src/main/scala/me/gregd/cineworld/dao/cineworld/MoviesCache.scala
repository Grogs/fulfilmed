package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.Movie
import scala.slick.session.Database
import me.gregd.cineworld.util.caching.{JavaSerialization, LoadingCache, DatabaseCache}
import JavaSerialization._
import java.io.{ByteArrayInputStream, ObjectInputStream}

class MoviesCache(val db: Database, val loader: (String) => List[Movie]) extends DatabaseCache[List[Movie]] with LoadingCache[List[Movie]] {
  val cacheName = "moviesForCinema"
  val deserialise = createDeserializer[List[Movie]]
  val serialise = createSerializer[List[Movie]]
}
