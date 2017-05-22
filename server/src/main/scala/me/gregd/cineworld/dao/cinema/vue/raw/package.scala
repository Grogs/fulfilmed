package me.gregd.cineworld.dao.cinema.vue.raw

package object model {

  object cinemas {
    case class VueCinema(
        name: String,
        search_term: String,
        search: String,
        selected: String,
        filteredname: String,
        id: String,
        hidden: Boolean
    )

    case class Venues(
        alpha: String,
        hidden: Boolean,
        cinemas: List[VueCinema]
    )

    case class VueCinemasResp(
        favourites: List[VueCinema],
        venues: List[Venues]
    )
  }

  object listings {
    case class Times(
        session_id: String,
        time: String,
        screen_type: String,
        hidden: Boolean
    )

    case class Showings(
        date_prefix: String,
        date_day: String,
        date_short: String,
        date_time: String,
        date_formatted: String,
        times: List[Times]
    )
    case class Genres(
        names: List[String],
        active: Boolean
    )
    case class Showing_type(
        name: String,
        active: Boolean
    )
    case class Films(
        showings: List[Showings],
        film_page_name: String,
        title: String,
        id: String,
        image_hero: String,
        image_poster: String,
        cert_image: String,
        cert_desc: String,
        synopsis_intro: String,
        synopsis_short: String,
        synopsis_full: String,
        info_release: String,
        info_runningtime_visible: Boolean,
        info_runningtime: String,
        info_director: String,
        info_cast: String,
        availablecopy: String,
        videolink: String,
        filmlink: String,
        timeslink: String,
        video: String,
        hidden: Boolean,
        coming_soon: Boolean,
        genres: Genres,
        showing_type: Showing_type
    )
    case class VueListingsResp(
        films: List[Films],
        dayOffset: Option[Double]
    )
  }

}
