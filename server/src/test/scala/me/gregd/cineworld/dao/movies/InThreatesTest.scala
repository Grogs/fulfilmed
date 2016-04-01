package me.gregd.cineworld.dao.movies

import com.google.inject.Guice
import me.gregd.cineworld.Config

/**
 * Created by Greg Dorrell on 03/05/2014.
 */
object InThreatesTest extends App {
  val movieDao = Guice.createInjector(Config).getInstance(classOf[Movies])

  private val movies = movieDao.openingSoon()
  movies foreach println
  println(movies.size)
  assert(!movies.isEmpty)
}
