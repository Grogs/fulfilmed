package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.Format
import scalaj.http.{HttpOptions, Http}
import org.json4s._
import org.json4s.native.JsonMethods._
import me.gregd.cineworld.dao.imdb.Ratings
import me.gregd.cineworld.dao.imdb.IMDbDao
import me.gregd.cineworld.domain.Movie
import me.gregd.cineworld.domain.Cinema
import me.gregd.cineworld.domain.Performance
import org.feijoas.mango.common.cache.{Cache, LoadingCache, CacheLoader, CacheBuilder}
import java.util.concurrent.TimeUnit._
import grizzled.slf4j.Logging
import me.gregd.cineworld.Config


/**
 * Author: Greg Dorrell
 * Date: 11/05/2013
 */
class Cineworld(apiKey:String, implicit val imdb: IMDbDao) extends CineworldDao with Logging {
  val movieCache : LoadingCache[String, List[Movie]] = {
    val loader = getMoviesUncached(_:String)(imdb)
    CacheBuilder.newBuilder()
      .refreshAfterWrite(1, HOURS)
      .build((key: String) => {
        logger.info(s"Retreiving list of Movies playing at Cineworld Cinema with ID: $key")
        loader(key)
      })
//      .build[String, List[Movie]]
  }


  implicit val formats = DefaultFormats
  val userAgent = ("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36")

  def getCinemas(): List[Cinema] = {
    val resp = Http("http://www.cineworld.com/api/quickbook/cinemas")
      .option(HttpOptions.connTimeout(30000))
      .option(HttpOptions.readTimeout(30000))
      .params("key" -> apiKey)
      .asString
    (parse(resp) \ "cinemas").children.map(_.extract[Cinema])
  }

  def getMovies(cinema: String)(implicit imdb: IMDbDao = this.imdb): List[Movie] = movieCache.get(cinema).get

  def getMoviesUncached(cinema: String)(implicit imdb: IMDbDao = this.imdb): List[Movie] = {
    val resp = Http("http://www.cineworld.com/api/quickbook/films")
      .option(HttpOptions.connTimeout(30000))
      .option(HttpOptions.readTimeout(30000))
      .params(
        "key" -> apiKey,
        "full" -> "true",
        "cinema"-> cinema
      )
      .headers(userAgent)
      .asString

    (parse(resp) \ "films").children
      .map(_.extract[Film])
      .map(_.toMovie)
  }

  def getPerformances(movie: String): List[Performance] = ???

}
object Cineworld extends Cineworld(Config.apiKey, Ratings) {}

case class Film(edi:String, title:String) {
  def toMovie(implicit imdb: IMDbDao = Config.imdb) = {
    val (format, title) = Format.split(this.title)
    val id = imdb.getId(title)
    val rating = id flatMap imdb.getIMDbRating
    val votes = id flatMap imdb.getVotes
    val audienceRating = imdb.getAudienceRating(title)
    val criticRating = imdb.getCriticRating(title)
    Movie(title, this.edi, format, id, rating, votes, audienceRating, criticRating)
  }
}
