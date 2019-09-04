package me.gregd.cineworld.integration

import play.api.libs.json.Json

package object tmdb {
  case class ImdbIdAndAltTitles(imdbId: Option[String], alternateTitles: List[String])

  import play.api.libs.json.OFormat

  case class TmdbMovie(poster_path: Option[String],
                       adult: Boolean,
                       overview: String,
                       release_date: String,
                       genre_ids: List[Double],
                       id: Long,
                       original_title: String,
                       original_language: String,
                       title: String,
                       backdrop_path: Option[String],
                       popularity: Double,
                       vote_count: Double,
                       video: Boolean,
                       vote_average: Double)

  case class TmdbTitle(iso_3166_1: String, title: String)

  case class DateRange(maximum: String, minimum: String)

  case class NowShowingResponse(page: Double, results: Vector[TmdbMovie], dates: DateRange, total_pages: Double, total_results: Double)

  object TmdbMovie {
    implicit val movieFormat = Json.format[TmdbMovie]
  }

  object TmdbTitle {
    implicit val tmdbTitleFormat = Json.format[TmdbTitle]
  }

  object DateRange {
    implicit val dateRangeFormat = Json.format[DateRange]
  }

  object NowShowingResponse {
    implicit val nowShowingRespFormat: OFormat[NowShowingResponse] = Json.format[NowShowingResponse]
  }
}
