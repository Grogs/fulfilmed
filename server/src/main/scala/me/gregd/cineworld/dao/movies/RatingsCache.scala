package me.gregd.cineworld.dao.movies

class RatingsCache(protected val values: collection.mutable.Map[String, (Double, Int)]) {

  def lookup(id: String) = values.get(id)

  def insert(id: String)(value: (Double, Int)) = {
    values += id -> value
  }
}
