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
import org.feijoas.mango.common.cache.{LoadingCache, CacheBuilder}
import java.util.concurrent.TimeUnit._
import grizzled.slf4j.Logging
import me.gregd.cineworld.Config


class Cineworld(apiKey:String, implicit val imdb: IMDbDao) extends CineworldDao with Logging {
  val movieCache : LoadingCache[String, List[Movie]] = {
    val loader = getMoviesUncached(_:String)(imdb)
    CacheBuilder.newBuilder()
      .refreshAfterWrite(1, HOURS)
      .build((key: String) => {
        logger.info(s"Retreiving list of Movies playing at Cineworld Cinema with ID: $key")
        loader(key)
      })
  }


  implicit val formats = DefaultFormats

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
      .asString

    logger.debug(s"Received listings for $cinema:\n$resp")

    (parse(resp) \ "films").children
      .map(_.extract[Film])
      .map(_.toMovie)
  }

  def getPerformances(movie: String): List[Performance] = ???

}
object Cineworld extends Cineworld(Config.apiKey, Ratings) {}

case class Film(edi:String, title:String) extends Logging {
  val textToStrip = List(" - Unlimited Screening", " (English subtitles)", " - Movies for Juniors")
  def toMovie(implicit imdb: IMDbDao = Config.imdb) = {
    logger.debug(s"Creating movie from $this")
    val (format, title) = Format.split(this.title)
    val cleanTitle = {
      var cleaned = title
      textToStrip foreach  { s =>
        cleaned = cleaned.replace(s,"")
      }
      cleaned
    }
    val id = imdb.getId(cleanTitle)
    val rating = id flatMap imdb.getIMDbRating
    val votes = id flatMap imdb.getVotes
    val audienceRating = imdb.getAudienceRating(cleanTitle)
    val criticRating = imdb.getCriticRating(cleanTitle)
    Movie(title, this.edi, format, id, rating, votes, audienceRating, criticRating)
  }
}
