package me.gregd.cineworld.dao.cineworld

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import cats.instances.future._
import cats.instances.tuple._
import cats.syntax.bitraverse._
import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.movies.MovieDao
import me.gregd.cineworld.domain.{Movie, Performance}
import org.json4s._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class RemoteCinemaDao @Inject()(
                                 imdb: MovieDao,
                                 tmdb: TheMovieDB,
                                 dao: CineworldRepository
                               ) extends CinemaDao with Logging {
  val decode = java.net.URLDecoder.decode(_: String, "UTF-8")
  implicit val formats = DefaultFormats
  implicit val ec = ExecutionContext.global

  private def getDate(s: String): LocalDate = s match {
    case "today" => LocalDate.now()
    case "tomorrow" => LocalDate.now() plusDays 1
    case other => LocalDate.parse(s)
  }


  override def retrieveCinemas() =
    dao.retrieveCinemas().map(
      _.map(CineworldRepository.toCinema)
    )

  override def retrieveMoviesAndPerformances(cinemaId: String, dateRaw: String) = {

    dao.retrieve7DayListings(cinemaId).flatMap { rawMovies =>
      val futures: Seq[Future[(Movie, List[Performance])]] = for {
        movieResp <- rawMovies
        (film, allPerformances) <- CineworldRepository.toMovie(cinemaId, movieResp)
        movie = imdb.toMovie(film)
        performances = allPerformances.getOrElse(getDate(dateRaw), Nil).toList
        if performances.nonEmpty
        performancesF = Future.successful(performances)
        res: Future[(Movie, List[Performance])] = sequence(movie -> performancesF)
        _ = logger.debug(s"Retrieved listings for $cinemaId:$dateRaw:${film.id}")
      } yield res
      logger.debug(futures)
      for (f <- futures) {
        logger.debug(s"processing $f")
        f.onComplete( t =>
          logger.debug(s"processed $f, res: $t")
        )
      }
      Future.sequence(
        futures
      ).map(_.toMap)
    }
  }


  def sequence[A, B](t: (Future[A], Future[B])): Future[(A, B)] = t match {
    case (a, b) =>
      for {
        a <- a
        b <- b
      } yield (a, b)
  }


  //  def retrieveMovies(cinema: String, date: LocalDate = new LocalDate)(implicit imdb: MovieDao = this.imdb): List[Movie] = movieCache.get(cinema, date).get
  //
  //  def retrievePerformances(cinema: String, date: LocalDate = new LocalDate): Map[String, Option[Seq[Performance]]] = performanceCache.get(cinema, date).get


  //  val movieCache: LoadingCache[(String, LocalDate), List[Movie]] = {
  //    val loader = getMoviesUncached(_: String, _: LocalDate)(imdb)
  //    CacheBuilder.newBuilder()
  //      .refreshAfterWrite(1, HOURS)
  //      .build((key: (String, LocalDate)) => {
  //        logger.info(s"Retreiving list of Movies playing at Cineworld Cinema with ID: $key")
  //        (loader.tupled) (key)
  //      })
  //  }

  //  val performanceCache: LoadingCache[(String, LocalDate), Map[String, Option[Seq[Performance]]]] = {
  //    val loader = getPerformancesUncached(_: String, _: LocalDate)
  //    CacheBuilder.newBuilder()
  //      .refreshAfterWrite(1, HOURS)
  //      .build((key: (String, LocalDate)) => {
  //        logger.info(s"Retreiving performances at $key today")
  //        (loader.tupled) (key)
  //      })
  //  }

  //  var cinemaCityCache: Map[String, Map[Film, List[Performance]]] = null

  //  def refreshCinemaCity() = cinemaCityCache = getCityCityRaw

  //  def cinemaCity: Map[String, Map[Film, List[Performance]]] = Option(cinemaCityCache) getOrElse {
  //    refreshCinemaCity
  //    cinemaCityCache
  //  }

  //  def retrieveCinemaCityCinemas(): List[Cinema] = {
  //    val resp = Http("http://www.cinemacity.hu/en/presentationsJSON")
  //      .option(HttpOptions.connTimeout(30000))
  //      .option(HttpOptions.readTimeout(30000))
  //      .params(
  //        "subSiteId" -> "0",
  //        "venueTypeId" -> "0",
  //        "showExpired" -> "true"
  //      ).asString
  //    if (!resp.isSuccess) logger.error(s"Unable to retreive cinemas, received: $resp")
  //    (parse(resp.body) \ "sites").children.map { site =>
  //      Cinema(id = (site \ "si").as[String], name = (site \ "sn").as[String])
  //    }
  //  }


  //  def retrieveOdeonCinemas(): Seq[Cinema] = {
  //    def getId(s: String): String = {
  //      ".*venue_id=(\\d+).*".r
  //        .unapplySeq(s)
  //        .head.head
  //    }
  //    val cinemas = Jsoup
  //      .connect("http://www.findanyfilm.com/find-cinemas?letter=O")
  //      .get
  //      .select("a.cinema_lnk")
  //      .iterator
  //      .asScala
  //      .filter(_.text contains "Odeon")
  //    cinemas.map(e =>
  //      Cinema(getId(e.attr("href")), e.text)
  //    ).toSeq
  //  }

  //  protected def getMoviesUncached(cinema: String, date: LocalDate = new LocalDate)(implicit imdb: MovieDao = this.imdb): List[Movie] = {
  //    cinema.toInt match {
  //      case id if id < 200 => retrieveFilms(cinema, Seq(date)).map(toMovie)
  //      case id if id > 1000000 => retrieveCinemaCityFilms(cinema, date).keys.map(toMovie).toList
  //      case id => retrieveOdeonFilms(cinema, date).keys.map(toMovie).toList
  //    }
  //
  //  }


  //  def retrieveOdeonFilms(cinema: String, date: LocalDate): Map[Film, Seq[Performance]] = {
  //    def getFilm(e: Element) = {
  //      val id = ".*~(\\d+)".r.unapplySeq(e.attr("href")).head.head
  //      val title = e.text.replaceFirst("\\(20[0-9]{2}\\)", "").replace("(Tc)", "").trim
  //      Film(id, title, "")
  //    }
  //    def getPerformance(e: Element) = {
  //      val bookingUrl = decode(".*redirect_url=(.*)$".r
  //        .unapplySeq(e.attr("href"))
  //        .head.head)
  //      Performance(e.text, available = true, "", bookingUrl)
  //    }
  //    val days = Days.daysBetween(new LocalDate, date).getDays + 1 //This will be 1 for today, or 2 for tomorrow
  //    Jsoup
  //      .connect(s"http://www.findanyfilm.com/find-a-cinema-3?day=$days&venue_id=$cinema&action=CinemaInfo")
  //      .get
  //      .select("div.times tr")
  //      .asScala
  //      .map(e =>
  //        getFilm(e.select("td.title > a").first) -> e.select("td.times > a").asScala.map(getPerformance).toSeq
  //      ).toMap
  //  }


  //  def retrieveCinemaCityFilms(cinema: String, date: LocalDate): Map[Film, Seq[Performance]] =
  //    cinemaCity(cinema)
  //      .mapValues(
  //        _.filter(
  //          _.date.forall(date.toString("yyyy-MM-dd").equals)
  //        )
  //      )


  //  def getCityCityRaw: Map[String, Map[Film, List[Performance]]] = {
  //    def getCinema(json: JValue): (String, Map[Film, List[Performance]]) = {
  //      (json \ "si").as[String] -> (json \ "fe").children.map(getFilm).toMap
  //    }
  //    def getFilm(json: JValue): (Film, List[Performance]) = {
  //      val id = (json \ "dc").as[String]
  //      val title = (json \ "fn").as[String].replaceFirst("\\(1?[268]E?\\)", "").trim
  //      val img = s"http://media1.cinema-city.pl/hu/Feats/med/$id.jpg"
  //      Film(id, title, img) -> (json \ "pr").children.map(getPerformance)
  //    }
  //    def getPerformance(json: JValue) = {
  //      val date = (json \ "dt").as[String].take(10).replaceAll("/", "-")
  //      Performance((json \ "tm").as[String], available = true, "", "http://www.cinemacity.hu/", Option(date))
  //    }
  //    val req = Http("http://www.cinemacity.hu/en/presentationsJSON")
  //      .option(HttpOptions.connTimeout(30000))
  //      .option(HttpOptions.readTimeout(30000))
  //      .params(
  //        "subSiteId" -> "0",
  //        "venueTypeId" -> "0",
  //        "showExpired" -> "true"
  //      )
  //
  //    val resp = req.asString
  //    if (!resp.isSuccess) logger.error(s"Unable to retreive films, received: $resp")
  //    val respStr = resp.body
  //
  //    logger.debug(s"Received cinema city listings:\n$respStr")
  //
  //    (parse(respStr) \ "sites").children.map(getCinema).toMap
  //  }


  //  def retrieveOdeonPerformances(cinema: String, date: LocalDate): Map[String, Option[Seq[Performance]]] = {
  //    retrieveOdeonFilms(cinema, date).map { case (f, perfs) =>
  //      f.id -> Option(perfs)
  //    }
  //  }

  //  protected def getPerformancesUncached(cinema: String, date: LocalDate = new LocalDate): Map[String, Option[Seq[Performance]]] = {
  //    cinema.toInt match {
  //      case id if id < 200 => getCineworldPerformances(cinema, date)
  //      case id if id > 1000000 => retrieveCinemaCityFilms(cinema, date).map {
  //        case (film, perfs) => (film.id, Option(perfs))
  //      }
  //      case id => retrieveOdeonPerformances(cinema, date)
  //    }
  //  }


}