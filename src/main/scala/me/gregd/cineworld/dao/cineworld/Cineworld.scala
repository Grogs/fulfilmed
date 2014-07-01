package me.gregd.cineworld.dao.cineworld

import me.gregd.cineworld.domain.{Performance, Format, Movie, Cinema}
import scalaj.http.{HttpOptions, Http}
import org.json4s._
import org.json4s.native.JsonMethods._
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.dao.movies.MovieDao
import org.feijoas.mango.common.cache.{LoadingCache, CacheBuilder}
import java.util.concurrent.TimeUnit._
import grizzled.slf4j.Logging
import me.gregd.cineworld.Config
import org.joda.time.{Days, LocalDate}
import me.gregd.cineworld.util.Implicits._
import scala.util.Try
import me.gregd.cineworld.dao.TheMovieDB
import org.jsoup.Jsoup
import collection.JavaConverters._
import java.net.URLDecoder.decode
import org.jsoup.nodes.Element

class Cineworld(apiKey:String, implicit val imdb: MovieDao) extends CineworldDao with Logging {
  val movieCache : LoadingCache[(String,LocalDate), List[Movie]] = {
    val loader = getMoviesUncached(_:String,_: LocalDate)(imdb)
    CacheBuilder.newBuilder()
      .refreshAfterWrite(1, HOURS)
      .build((key: (String, LocalDate)) => {
        logger.info(s"Retreiving list of Movies playing at Cineworld Cinema with ID: $key")
        loader.tupled(key)
      })
  }

  val performanceCache : LoadingCache[(String,LocalDate), Map[String, Option[Seq[Performance]]]] = {
    val loader = getPerformancesUncached(_:String, _:LocalDate)
    CacheBuilder.newBuilder()
      .refreshAfterWrite(1, HOURS)
      .build((key: (String,LocalDate)) => {
        logger.info(s"Retreiving performances at $key today")
        loader.tupled(key)
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


  def getOdeonCinemas(): Seq[Cinema] = {
    def getId(s: String): String = {
      ".*venue_id=(\\d+).*".r
        .unapplySeq(s)
        .head.head
    }
    val cinemas = Jsoup
      .connect("http://www.findanyfilm.com/find-cinemas?letter=O")
      .get
      .select("a.cinema_lnk")
      .iterator
      .asScala
      .filter(_.text contains "Odeon")
    cinemas.map( e =>
      Cinema( getId(e.attr("href")) , e.text)
    ).toSeq
  }

  def getMovies(cinema: String, date: LocalDate = new LocalDate)(implicit imdb: MovieDao = this.imdb): List[Movie] = movieCache.get(cinema, date).get


  protected def getMoviesUncached(cinema: String, date: LocalDate = new LocalDate)(implicit imdb: MovieDao = this.imdb): List[Movie] = {
    cinema.toInt match {
      case id if (id < 200) => getFilms(cinema,Seq(date)).map(_.toMovie)
      case id => getOdeonFilms(cinema, date).map(_._1.toMovie).toList
    }

  }


  def getOdeonFilms(cinema: String, date: LocalDate): Seq[(Film, Seq[Performance])] = {
    def getFilm(e: Element) = {
      val id = ".*~(\\d+)".r.unapplySeq(e.attr("href")).head.head
      val title = e.text.replaceFirst("\\(20[0-9]{2}\\)","").replace("(Tc)","").trim
      Film(id, title, "")
    }
    def getPerformance(e: Element) = {
      val bookingUrl = decode(".*redirect_url=(.*)$".r
        .unapplySeq(e.attr("href"))
        .head.head)
      Performance(e.text, true, "", bookingUrl)
    }
    val days = Days.daysBetween(new LocalDate, date).getDays + 1  //This will be 1 for today, or 2 for tomorrow
    Jsoup
      .connect(s"http://www.findanyfilm.com/find-a-cinema-3?day=$days&venue_id=$cinema&action=CinemaInfo")
      .get
      .select("div.times tr")
      .asScala
      .map( e =>
        getFilm(e.select("td.title > a").first) -> e.select("td.times > a").asScala.map(getPerformance).toSeq
      ).toSeq
  }

  def getFilms(cinema: String, dates: Seq[LocalDate] = Seq(new LocalDate)): List[Film] = {
    val req = Http("http://www.cineworld.com/api/quickbook/films")
      .option(HttpOptions.connTimeout(30000))
      .option(HttpOptions.readTimeout(30000))
      .params(
        dates.map(
          "date" -> _.toString("yyyyMMdd")
        ) ++ Seq(
          "key" -> apiKey,
          "full" -> "true",
          "cinema" -> cinema
        ) : _*
      )

    val resp = req.asString

    logger.debug(s"Received listings for $cinema:\n$resp")

    (parse(resp) \ "films").children
      .map(_.extract[Film])
  }

  def getPerformances(cinema: String, date: LocalDate = new LocalDate): Map[String, Option[Seq[Performance]]] = performanceCache.get(cinema, date).get

  def getOdeonPerformances(cinema: String, date: LocalDate): Map[String, Option[Seq[Performance]]] = {
    getOdeonFilms(cinema, date).map{ case (f,perfs) =>
      f.edi -> Option(perfs)
    }.toMap
  }

  protected def getPerformancesUncached(cinema: String, date: LocalDate = new LocalDate): Map[String, Option[Seq[Performance]]] = {
    cinema.toInt match {
      case id if (id < 200) => getCineworldPerformances(cinema, date)
      case id => getOdeonPerformances(cinema, date)
    }


  }


  def getCineworldPerformances(cinema: String, date: LocalDate): Map[String, Option[List[Performance]]] = {
    def performances(filmEdi: String) = Try {
      val resp = Http("http://www.cineworld.com/api/quickbook/performances")
        .option(HttpOptions.connTimeout(30000))
        .option(HttpOptions.readTimeout(30000))
        .params(
          "key" -> apiKey,
          "film" -> filmEdi,
          "date" -> date.toString("yyyyMMdd"),
          "cinema" -> cinema
        )
        .asString
      logger.debug(s"Received performance for $filmEdi on $date at $cinema:\n$resp")
      (parse(resp) \ "performances").children map (_.extract[Performance])
    }.toOption

    (getMovies(cinema).threads(10) map (_.cineworldId.get) map (id => id -> performances(id)) toMap).seq
  }
}
object Cineworld extends Cineworld(Config.apiKey, Movies) {}

case class Film(edi:String, title:String, poster_url: String) extends Logging {
  val textToStrip = List(" - Unlimited Screening", " (English subtitles)", " - Movies for Juniors", "Take 2 - ", "3D - ", "2D - ", "Autism Friendly Screening: ", " for Juniors", " (English dubbed version)", " (Japanese with English subtitles)")
  def cleanTitle = {
    var cleaned = title
    textToStrip foreach  { s =>
      cleaned = cleaned.replace(s,"")
    }
    cleaned
  }
  def toMovie(implicit imdb: MovieDao = Config.imdb, tmdb: TheMovieDB = Config.tmdb) = {
    logger.debug(s"Creating movie from $this")
    val format = Format.split(this.title)._1
    val movie:Movie = imdb
      .find(cleanTitle)
      .getOrElse(
        Movie(cleanTitle,None,None,None,None,None,None, None, None)
      )
      .copy(
        title = title,
        cineworldId = Option(edi),
        format = Option(format),
        posterUrl = Option(poster_url)
      )
    val id = movie.imdbId map ("tt"+_)
    val rating = id flatMap imdb.getIMDbRating
    val votes = id flatMap imdb.getVotes
    movie
      .copy(rating = rating, votes = votes)
      //Use higher res poster for TMDB when available
      .copy(posterUrl = TheMovieDB.posterUrl(movie) orElse movie.posterUrl )
  }
}

