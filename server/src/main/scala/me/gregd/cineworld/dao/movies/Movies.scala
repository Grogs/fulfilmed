package me.gregd.cineworld.dao.movies

import com.typesafe.scalalogging.LazyLogging
import info.debatty.java.stringsimilarity.SorensenDice
import me.gregd.cineworld.config.MoviesConfig
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.model.TmdbMovie
import me.gregd.cineworld.dao.ratings.{Ratings, RatingsResult}
import me.gregd.cineworld.domain.{Film, Format, Movie}
import monix.execution.FutureUtils.extensions._
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class Movies(tmdb: TheMovieDB, ratings: Ratings, config: MoviesConfig) extends MovieDao with LazyLogging {

  def toMovie(film: Film): Future[Movie] = {
    logger.debug(s"Creating movie from $film")

    for {
      movieOpt <- find(film.cleanTitle)
      movie = movieOpt.getOrElse(Movie(film.title))
      format = Format.split(film.title)._1
      poster = movie.posterUrl orElse Option(film.poster_url).filter(!_.isEmpty)
    } yield
      movie.copy(
        title = film.cleanTitle,
        cineworldId = Option(film.id),
        format = Option(format),
        posterUrl = poster
      )

  }

  private var cachedMovies: Future[Seq[Movie]] = Future.failed(new UninitializedError)
  private var lastCached: Long = 0

  def refresh(): Future[Unit] = {
    logger.info(s"refreshing movies cache, old value:\n$cachedMovies")
    val start = System.currentTimeMillis()
    cachedMovies = allMovies()
    lastCached = System.currentTimeMillis()

    def elapsed = (System.currentTimeMillis() - start) / 1000

    cachedMovies.transform {
      case Failure(ex) =>
        logger.error(s"Failed to update movie cache (after $elapsed seconds)", ex)
        Failure(ex)
      case Success(movies) =>
        logger.info(s"Movies cache updates with ${movies.length} items. Took $elapsed seconds.")
        Success(())
    }
  }

  def allMoviesCached() = {
    val age = System.currentTimeMillis() - lastCached
    this.synchronized(
      if (age.millis > 10.hours) refresh()
    )
    cachedMovies
  }.timeoutTo(config.cacheTimeout, Future.successful(Seq.empty))

  private def allMovies(): Future[Seq[Movie]] = {
    collapse(
      for {
        nowPlaying <- tmdb.fetchMovies()
        _ = logger.info("Fetched now playing")
      } yield
        for {
          tmdbMovie <- nowPlaying.distinct
          tmdbId = tmdbMovie.id.toString
        } yield
          for {
            alternateTitles <- tmdb.alternateTitles(tmdbId)
            imdbId <- tmdb.fetchImdbId(tmdbId)
            ratingsResult <- imdbId.map(ratings.fetchRatings).getOrElse(Future.successful(RatingsResult(None, None, None, None)))
          } yield
            for {
              altTitle <- (tmdbMovie.title :: tmdbMovie.original_title :: alternateTitles).distinct
            } yield toMovie(tmdbMovie.copy(title = altTitle), imdbId, ratingsResult)
    )
  }

  private def toMovie(tmdbMovie: TmdbMovie, imdbId: Option[String], ratingsResult: RatingsResult) = {
    Movie(
      tmdbMovie.title,
      None,
      None,
      imdbId,
      Option(tmdbMovie.id.toString),
      ratingsResult.imdbRating,
      ratingsResult.imdbVotes,
      Option(tmdbMovie.vote_average),
      Option(tmdbMovie.vote_count.toInt),
      tmdbMovie.poster_path.map(tmdb.baseImageUrl + _),
      ratingsResult.metascore,
      ratingsResult.rottenTomatoes
    )
  }

  val compareFunc: (String, String) => Double =
    new SorensenDice(1).similarity(_: String, _: String)

  val minWeight = 0.8

  def find(title: String): Future[Option[Movie]] = for (allMovies <- allMoviesCached()) yield {
    if (allMovies.nonEmpty) {
      val matc = allMovies.maxBy(m => compareFunc(title, m.title))
      val weight = compareFunc(title, matc.title)
      val accept = weight > minWeight
      logger.debug(s"Best match for $title was  ${matc.title} ($weight) - ${if (accept) "ACCEPTED" else "REJECTED"}")
      if (accept) {
        Option(matc)
      } else {
        logger.info(s"No match found for '$title', nearest match was ${matc.title} which scored $weight'")
        None
      }
    } else
      None
  }

  private def sequence[A, B](ot: Option[(A, B)]): (Option[A], Option[B]) = {
    ot match {
      case None         => None -> None
      case Some((a, b)) => Option(a) -> Option(b)
    }
  }

  private def collapse[T](fsfsT: Future[Seq[Future[Seq[T]]]]): Future[Seq[T]] =
    fsfsT.map(sfsT => Future.sequence(sfsT).map(_.flatten)).flatMap(identity)

}
