package me.gregd.cineworld.domain.service

import cats.effect.{IO, Ref}
import cats.implicits.toTraverseOps
import info.debatty.java.stringsimilarity.SorensenDice
import me.gregd.cineworld.config.MoviesConfig
import me.gregd.cineworld.domain.model.{Film, Movie}
import me.gregd.cineworld.integration.omdb.{OmdbService, RatingsResult}
import me.gregd.cineworld.integration.tmdb.{TmdbMovie, TmdbService}
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration._

class MovieService(tmdb: TmdbService, ratings: OmdbService, config: MoviesConfig) {
  private val logger = Slf4jLogger.getLogger[IO]

  def toMovie(film: Film): IO[Movie] = {

    val title = cleanTitle(film)

    for {
      _        <- logger.debug(s"Creating movie from $film")
      movieOpt <- find(title)
      movie  = movieOpt.getOrElse(Movie(film.title))
      format = split(film.title)._1
      poster = movie.posterUrl orElse Option(film.poster_url).filter(!_.isEmpty)
    } yield
      movie.copy(
        title = title,
        cineworldId = Option(film.id),
        format = Option(format),
        posterUrl = poster
      )

  }

  private val cachedMovies: Ref[IO, Seq[Movie]] = {
    import cats.effect.unsafe.IORuntime.global
    Ref.ofEffect[IO, Seq[Movie]](IO.pure(Nil)).unsafeRunSync()(global)
  }
  private val lastCached: Ref[IO, FiniteDuration] = Ref.unsafe(FiniteDuration(0, "seconds"))

  def refresh: IO[Unit] = {
    for {
      oldCached <- cachedMovies.get
      _       <- logger.info(s"refreshing movies cache, old value:\n$oldCached")
      start   <- IO.realTime
      attempt <- allMovies().attempt
      elapsed <- IO.realTime
      _ <- attempt match {
        case Left(ex) =>
          logger.error(ex)(s"Failed to update movie cache (after $elapsed seconds)") >>
            IO.raiseError(ex)
        case Right(movies) =>
          logger.info(s"Movies cache updates with ${movies.length} items. Took $elapsed seconds.") >>
            cachedMovies.set(movies) >>
            IO.realTime.flatMap(lastCached.set)
      }
    } yield ()
  }

  def allMoviesCached() = {
    for {
      now                 <- IO.realTime
      lastCachedTimestamp <- lastCached.get
      age = now.minus(lastCachedTimestamp)
      _      <- if (age > 10.hours) refresh.start else IO.unit
      movies <- cachedMovies.get
    } yield movies
  }.timeoutTo(config.cacheTimeout, IO.pure(Seq.empty))

  private def allMovies(): IO[Vector[Movie]] = {
    collapse(
      for {
        nowPlaying <- tmdb.fetchMovies()
        _          <- logger.info("Fetched now playing")
      } yield
        for {
          tmdbMovie <- nowPlaying.distinct
          tmdbId = tmdbMovie.id.toString
        } yield
          for {
            alternateTitles <- tmdb.alternateTitles(tmdbId)
            imdbId          <- tmdb.fetchImdbId(tmdbId)
            ratingsResult   <- imdbId.map(ratings.fetchRatings).getOrElse(IO.pure(RatingsResult(None, None, None, None)))
            _               <- logger.debug(s"Fetched data for '${tmdbMovie.title}'")
          } yield
            for {
              altTitle <- (tmdbMovie.title +: tmdbMovie.original_title +: alternateTitles).distinct
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

  def find(title: String): IO[Option[Movie]] = allMoviesCached().flatMap { allMovies =>
    if (allMovies.nonEmpty) {
      val matc   = allMovies.maxBy(m => compareFunc(title, m.title))
      val weight = compareFunc(title, matc.title)
      val accept = weight > minWeight
      logger.debug(s"Best match for $title was  ${matc.title} ($weight) - ${if (accept) "ACCEPTED" else "REJECTED"}") >> (
        if (accept) {
          IO.pure(Option(matc))
        } else {
          logger.info(s"No match found for '$title', nearest match was ${matc.title} which scored $weight'") >>
            IO.pure(None)
        }
      )
    } else
      IO.pure(None)
  }

  private def collapse[T](ivivT: IO[Vector[IO[Vector[T]]]]): IO[Vector[T]] =
    ivivT.map(_.sequence.map(_.flatten)).flatMap(identity)

  private def split(title: String) = {
    title.take(5) match {
      case "2D - " | "(2D) " => ("2D", title.substring(5))
      case "3D - "           => ("3D", title.substring(5))
      case _                 => ("default", title)
    }
  }

  private val textToStrip = List(
    " - Unlimited Screening",
    " (English subtitles)",
    ": Movies For Juniors",
    " - Movies For Juniors",
    " Movies For Juniors",
    "Take 2 - ",
    "3D - ",
    "2D - ",
    "Autism Friendly Screening: ",
    " for Juniors",
    " (English dubbed version)",
    " (Japanese with English subtitles)",
    " (Punjabi)",
    " (Hindi)",
    " Unlimited Screening"
  )
  private def cleanTitle(f: Film) = textToStrip.foldLeft(f.title)((res, r) => res.replace(r, ""))

}
