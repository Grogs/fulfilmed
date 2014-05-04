package me.gregd.cineworld.dao.movies

/**
 * Created by Greg Dorrell on 03/05/2014.
 */
object InThreatesTest extends App {
  private val movies = Movies.nowShowingRT()
  movies foreach println
  println(movies.size)
  assert(!movies.isEmpty)
}
