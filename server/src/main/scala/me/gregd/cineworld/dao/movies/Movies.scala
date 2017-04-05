package me.gregd.cineworld.dao.movies

import javax.inject.{Inject, Singleton}

import com.rockymadden.stringmetric.similarity.DiceSorensenMetric
import grizzled.slf4j.Logging
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.domain.{Film, Format, Movie}
import org.json4s._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

@Singleton
class Movies @Inject()(tmdb: TheMovieDB, ratings: Ratings) extends MovieDao with Logging {

  implicit val formats = DefaultFormats

  def toMovie(film: Film): Future[Movie] = {
    logger.debug(s"Creating movie from $film")

    def ratingAndVotes(imdbId: Option[String]): Future[(Option[Double], Option[Int])] = {
      Future.traverse(imdbId.toSeq)(ratings.ratingAndVotes).map(_.headOption.flatten).map(sequence)
    }

    for {
      movieOpt <- find(film.cleanTitle)
      movie = movieOpt.getOrElse(Movie(film.title))
      imdbId = movie.imdbId map ("tt" + _)
      (rating, votes) <- ratingAndVotes(imdbId)
      format = Format.split(film.title)._1
    } yield
      movie.copy(
        title = film.title,
        cineworldId = Option(film.id),
        format = Option(format),
        posterUrl = Option(film.poster_url),
        rating = rating,
        votes = votes
      )

  }

  private var cachedMovies: Future[Seq[Movie]] = Future.failed(new UninitializedError)
  private var lastCached: Long = 0
  def allMoviesCached() = {
    def refresh = {
      logger.info(s"refreshing movies cache, old value:\n$cachedMovies")
      cachedMovies = allMovies()
      lastCached = System.currentTimeMillis()
    }
    val age = System.currentTimeMillis() - lastCached
    if (age.millis > 10.hours) refresh
    cachedMovies
  }

  def allMovies(): Future[Seq[Movie]] = collapse(
    for {
      nowPlaying <- tmdb.fetchNowPlaying()
    } yield
      for {
        movie <- nowPlaying
      } yield
        for {
          alternateTitles <- tmdb.alternateTitles(movie.id.toString)
          imdbId <- tmdb.fetchImdbId(movie.id.toString)
        } yield
          for {
            altTitle <- (movie.title :: alternateTitles).distinct
          } yield
            Movie(
              movie.title,
              None,
              imdbId,
              None,
              Option(movie.id.toString),
              None,
              None,
              Option(movie.vote_average),
              Option(movie.vote_count.toInt),
              movie.poster_path.map(tmdb.baseImageUrl + _)
            )
  )

  val compareFunc: (String, String) => Double =
    DiceSorensenMetric(1).compare(_: String, _: String).get

  val minWeight = 0.8

  def find(title: String): Future[Option[Movie]] = for (allMovies <- allMoviesCached()) yield {
    val matc = allMovies.maxBy(m => compareFunc(title, m.title))
    val weight = compareFunc(title, matc.title)
    logger.debug(s"Best match for $title was  ${matc.title} ($weight) - ${if (weight > minWeight) "ACCEPTED" else "REJECTED"}")
    if (weight > minWeight) Option(matc) else None
  }

  private def sequence[A, B](ot: Option[(A, B)]): (Option[A], Option[B]) = {
    ot match {
      case None => None -> None
      case Some((a, b)) => Option(a) -> Option(b)
    }
  }

  private def collapse[T](fsfsT: Future[Seq[Future[Seq[T]]]]): Future[Seq[T]] =
    fsfsT.map( sfsT => Future.sequence(sfsT).map(_.flatten)).flatMap(identity)

}
