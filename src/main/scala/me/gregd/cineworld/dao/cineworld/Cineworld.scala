package me.gregd.cineworld.dao.cineworld

import com.typesafe.scalalogging.slf4j.StrictLogging
import me.gregd.cineworld.domain.{Performance, Format, Movie, Cinema}
import scalaj.http.{HttpResponse, HttpOptions, Http}
import org.json4s._
import org.json4s.native.JsonMethods._
import me.gregd.cineworld.dao.movies.Movies
import me.gregd.cineworld.dao.movies.MovieDao
import org.feijoas.mango.common.cache.{LoadingCache, CacheBuilder}
import java.util.concurrent.TimeUnit._
import me.gregd.cineworld.Config
import org.joda.time.{LocalDate, Days}
import me.gregd.cineworld.util.Implicits._
import scala.util.Try
import me.gregd.cineworld.dao.TheMovieDB
import org.jsoup.Jsoup
import collection.JavaConverters._
import org.jsoup.nodes.Element
import org.json4s.DefaultReaders.StringReader
import java.time.{LocalDate=>JavaLocalDate}


class Cineworld(apiKey:String, implicit val imdb: MovieDao) extends CineworldDao with StrictLogging {
  val decode = java.net.URLDecoder.decode(_:String, "UTF-8")

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

  var cinemaCityCache: Map[String, Map[Film,List[Performance]]] = null
  def refreshCinemaCity() = cinemaCityCache = getCityCityRaw
  def cinemaCity: Map[String, Map[Film,List[Performance]]] = Option(cinemaCityCache) getOrElse {
    refreshCinemaCity
    cinemaCityCache
  }

  implicit val formats = DefaultFormats

  def retrieveCinemas(): List[Cinema] = {
    val resp = Http("http://www2.cineworld.co.uk/api/quickbook/cinemas")
      .option(HttpOptions.connTimeout(30000))
      .option(HttpOptions.readTimeout(30000))
      .params("key" -> apiKey)
      .asString
    if (!resp.isSuccess) logger.error(s"Unable to retreive cinemas, received: $resp")
    (parse(resp.body) \ "cinemas").children.map(_.extract[Cinema])
  }


  def retrieveCinemaCityCinemas(): List[Cinema] = {
    val resp = Http("http://www.cinemacity.hu/en/presentationsJSON")
      .option(HttpOptions.connTimeout(30000))
      .option(HttpOptions.readTimeout(30000))
      .params(
        "subSiteId" -> "0",
        "venueTypeId" -> "0",
        "showExpired" -> "true"
      ).asString
    if (!resp.isSuccess) logger.error(s"Unable to retreive cinemas, received: $resp")
    (parse(resp.body) \ "sites").children.map{ site =>
      Cinema(id = (site \ "si").as[String], name = (site \ "sn").as[String])
    }
  }


  def retrieveOdeonCinemas(): Seq[Cinema] = {
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

  def retrieveMovies(cinema: String, date: LocalDate = new LocalDate)(implicit imdb: MovieDao = this.imdb): List[Movie] = movieCache.get(cinema, date).get


  protected def getMoviesUncached(cinema: String, date: LocalDate = new LocalDate)(implicit imdb: MovieDao = this.imdb): List[Movie] = {
    cinema.toInt match {
      case id if id < 200 => retrieveFilms(cinema,Seq(date)).map(_.toMovie)
      case id if id > 1000000 => retrieveCinemaCityFilms(cinema, date).map(_._1.toMovie).toList
      case id => retrieveOdeonFilms(cinema, date).map(_._1.toMovie).toList
    }

  }


  def retrieveOdeonFilms(cinema: String, date: LocalDate): Seq[(Film, Seq[Performance])] = {
    def getFilm(e: Element) = {
      val id = ".*~(\\d+)".r.unapplySeq(e.attr("href")).head.head
      val title = e.text.replaceFirst("\\(20[0-9]{2}\\)","").replace("(Tc)","").trim
      Film(id, title, "")
    }
    def getPerformance(e: Element) = {
      val bookingUrl = decode(".*redirect_url=(.*)$".r
        .unapplySeq(e.attr("href"))
        .head.head)
      Performance(e.text, available = true, "", bookingUrl)
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
  
  
  def retrieveCinemaCityFilms(cinema: String, date: LocalDate): Seq[(Film, Seq[Performance])] =
    cinemaCity(cinema)
      .mapValues(
        _.filter(
          _.date.map(date.equals)
            .getOrElse(true)
        )
      ).toSeq


  def getCityCityRaw: Map[String, Map[Film,List[Performance]]] = {
    def getCinema(json: JValue): (String, Map[Film,List[Performance]])= {
      (json \ "si").as[String] -> (json \ "fe").children.map(getFilm).toMap
    }
    def getFilm(json: JValue): (Film,List[Performance]) = {
      val id = (json \ "dc").as[String]
      val title = (json \ "fn").as[String].replaceFirst("\\(1?[268]E?\\)","").trim
      val img = s"http://media1.cinema-city.pl/hu/Feats/med/$id.jpg"
      Film(id, title, img) -> (json \ "pr").children.map(getPerformance)
    }
    def getPerformance(json: JValue) = {
      val dateStr = (json \ "dt").as[String].take(10).replaceAll("/","-")
      val date = JavaLocalDate.parse(dateStr)
      Performance((json \ "tm").as[String], available = true, "", "http://www.cinemacity.hu/", Option(date))
    }
    val req = Http("http://www.cinemacity.hu/en/presentationsJSON")
      .option(HttpOptions.connTimeout(30000))
      .option(HttpOptions.readTimeout(30000))
      .params(
          "subSiteId" -> "0",
          "venueTypeId" -> "0",
          "showExpired" -> "true"
      )

    val resp = req.asString
    if (!resp.isSuccess) logger.error(s"Unable to retreive films, received: $resp")
    val respStr = resp.body

    logger.debug(s"Received cinema city listings:\n$respStr")

    (parse(respStr) \ "sites").children.map(getCinema).toMap
  }



  def retrieveFilms(cinema: String, dates: Seq[LocalDate] = Seq(new LocalDate)): List[Film] = {
    val req = Http("http://www2.cineworld.co.uk/api/quickbook/films")
      .option(HttpOptions.connTimeout(30000))
      .option(HttpOptions.readTimeout(30000))
      .params(
        dates.map(
          "date" -> _.toString("yyyyMMdd")
        ) ++ Seq(
          "key" -> apiKey,
          "full" -> "true",
          "cinema" -> cinema
        )
      )

    val resp = req.asString
    if (!resp.isSuccess) logger.error(s"Unable to retreive films, received: $resp")
    val respStr = resp.body

    logger.debug(s"Received listings for $cinema:\n$respStr")

    (parse(respStr) \ "films").children
      .map(_.extract[Film])
  }

  def retrievePerformances(cinema: String, date: LocalDate = new LocalDate): Map[String, Option[Seq[Performance]]] = performanceCache.get(cinema, date).get

  def retrieveOdeonPerformances(cinema: String, date: LocalDate): Map[String, Option[Seq[Performance]]] = {
    retrieveOdeonFilms(cinema, date).map{ case (f,perfs) =>
      f.edi -> Option(perfs)
    }.toMap
  }

  protected def getPerformancesUncached(cinema: String, date: LocalDate = new LocalDate): Map[String, Option[Seq[Performance]]] = {
    cinema.toInt match {
      case id if id < 200 => getCineworldPerformances(cinema, date)
      case id if id > 1000000 => retrieveCinemaCityFilms(cinema, date).toMap.map{
        case (film, perfs) => (film.edi, Option(perfs))
      }
      case id => retrieveOdeonPerformances(cinema, date)
    }
  }


  def getCineworldPerformances(cinema: String, date: LocalDate): Map[String, Option[List[Performance]]] = {
    def performances(filmEdi: String) = Try {
      val resp = Http("http://www2.cineworld.co.uk/api/quickbook/performances")
        .option(HttpOptions.connTimeout(30000))
        .option(HttpOptions.readTimeout(30000))
        .params(
          "key" -> apiKey,
          "film" -> filmEdi,
          "date" -> date.toString("yyyyMMdd"),
          "cinema" -> cinema
        )
        .asString
      if (!resp.isSuccess) logger.error(s"Unable to retreive performances, received: $resp")
      val respStr = resp.body
      logger.debug(s"Received performance for $filmEdi on $date at $cinema:\n$respStr")
      (parse(respStr) \ "performances").children map (_.extract[Performance]) filterNot (_.`type` == "star")
    }.toOption

    (retrieveMovies(cinema).threads(10) map (_.cineworldId.get) map (id => id -> performances(id)) toMap).seq
  }
}
object Cineworld extends Cineworld(Config.apiKey, Movies) {}

case class Film(edi:String, title:String, poster_url: String) extends StrictLogging {
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
      .copy(
        posterUrl = try {
          TheMovieDB.posterUrl(movie)
        } catch {
          case e =>
            logger.error("TMDB Failure",e)
            movie.posterUrl
        }
      )
  }
}

