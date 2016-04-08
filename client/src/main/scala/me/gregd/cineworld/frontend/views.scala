package me.gregd.cineworld.frontend

import japgolly.scalajs.react.ReactComponentB
import me.gregd.cineworld.domain.{Movie,Performance}
import japgolly.scalajs.react.vdom.prefix_<^._


package object views {

  val FilmCard = {
    ReactComponentB[(Movie, List[Performance])]("FilmCard")
      .render_P{ case (m, pl) =>
        <.div( ^.className := "film-container",
          <.div( ^.classSet1("film", "threedee" -> (m.format == "3D")),
            <.div(^.`class` := "title", m.title),
            <.div(^.`class` := "ratings",
              <.div(^.`class` := "rating imdb", m.rating),
              <.div(^.`class` := "rating rt", m.criticRating),
              <.div(^.`class` := "rating rtAudience", m.audienceRating)
            ),
            <.div(^.`class` := "times",
              for (p <- pl) yield
                <.a(^.href := p.booking_url,
                  <.div(^.`class` := "time", p.time)
                )
            )
          ),
          <.img( ^.`class`:="film-background", ^.src := m.posterUrl)
        )
      }
      .build
  }

  val FilmsList = {
    def icon(faClasses: String, message: String) = {
      <.div(^.margin := "50px 0 50px 0", ^.color.white,
        <.i(^.`class` := s"fa $faClasses fa-5x"),
        <.div(^.`class`:="label", message)
      )
    }
    def spinner = icon("fa-refresh fa-spin", "Loading movies")
    def frown = icon("fa-frown-o", "No movies found!")
    ReactComponentB[FilmsState]("FilmsList")
    .render_P{
      case FilmsState(loading, movies) =>
        if (loading)
          spinner
        else if (movies.isEmpty)
          frown
        else
          <.div( ^.className := "films-list-container",
            for (m <- movies) yield FilmCard(m)
          )
    }
    .build
  }

  val FilmPage = {
    ReactComponentB[FilmsState]("FilmPage")
      .render_P{ s =>
        val sortSelection = <.div(^.`class` := "menu-group",
          <.i(^.`class` := "fa fa-sort-alpha-asc fa-lg", ^.color.white),
          <.select(^.id := "ordering", ^.`class` := "menu", ^.onChange := "TODO",
            <.option(^.value := "?", ^.selected := "selected", ^.disabled := "disabled", "Order by..."),
            <.option(^.value := "imdb", "IMDb Rating (Descending)"),
            <.option(^.value := "critic", "RT Critic Rating (Descending)"),
            <.option(^.value := "audience", "RT Audience Rating (Descending)"),
            <.option(^.value := "showtime", "Next Showing")))
        val dateSelection = <.div(^.`class` := "menu-group",
          <.i(^.`class` := "fa fa-calendar fa-lg", ^.color.white),
          <.select(^.id := "date", ^.`class` := "menu", ^.onChange := "TODO",
            <.option(^.value := "today", ^.selected := "selected", "Today"),
            <.option(^.value := "tomorrow", "Tomorrow")))
        val menu = <.header(<.div(^.`class` := "menu", dateSelection, sortSelection))
        val attribution = <.div(^.id := "attribution",
          "Powered by: ",
          <.a(^.href := "http://www.cineworld.co.uk/", "Cineworld's API"), ", ",
          <.a(^.href := "http://www.omdbapi.com/", "The OMDb API"), ", ",
          <.a(^.href := "http://www.themoviedb.org/", "TheMovieDB"), " and ",
          <.a(^.href := "http://www.rottentomatoes.com/", "Rotten Tomatoes"))

        <.div( ^.id:="films",
          menu,
          FilmsList(s),
          attribution
        )
      }
      .build
  }

}
