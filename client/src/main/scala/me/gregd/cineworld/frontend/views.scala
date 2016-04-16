package me.gregd.cineworld.frontend

import autowire._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import me.gregd.cineworld.domain.{CinemaApi, Movie, Performance}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue


package object views {

  val FilmCard = {
    ReactComponentB[(Movie, List[Performance])]("FilmCard")
      .render_P{ case (m, pl) =>
        <.div( ^.className := "film-container",
          <.div( ^.classSet1("film", "threedee" -> (m.format == "3D")),
            <.div(^.`class` := "title", m.title),
            <.div(^.`class` := "ratings",
              <.div(^.`class` := "rating imdb", <.a(^.href:=m.imdbId.map("http://www.imdb.com/title/tt" + _), m.rating)),
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
      <.div(^.margin := "50px 0 50px 0", ^.color.white, ^.textAlign.center,
        <.i(^.`class` := s"fa $faClasses fa-5x"),
        <.div(^.`class`:="label", message)
      )
    }
    def spinner = icon("fa-refresh fa-spin", "Loading movies")
    def frown = icon("fa-frown-o", "No movies found!")
    ReactComponentB[FilmsState]("FilmsList")
    .render_P{
      case FilmsState(loading, cienma, unorderedMovie, sort, dates, selectedDate) =>
        val movies = unorderedMovie.toSeq.sorted(sort.ordering)
        if (unorderedMovie.isEmpty)
          if (loading)
            spinner
          else
            frown
        else
          <.div( ^.className := "films-list-container",
            for (m <- movies)
              yield FilmCard(m)
          )
    }
    .build
  }

  class FilmsBackend($: BackendScope[Unit, FilmsState]) {

    def sortBy(sort: Sort) = $.modState(_.copy(sort = sort))

    def loadDate(date: Date) = $.state.map(_.cinema) >>= (cinema => load(date, cinema))
    def loadCinema(cinema: String) = $.state.map(_.selectedDate) >>= (date => load(date, cinema))

    def load(date: Date, cinema: String) = {
      val clear = $.modState(_.copy(isLoading = true, films = Map.empty))
      val updateDate = $.modState(_.copy(selectedDate = date))
      val res = Client[CinemaApi].getMoviesAndPerformances("66", "tomorrow").call()
      val updateMovies = for (movies <- res) yield $.modState(_.copy(isLoading = false, films = movies))
      clear >> updateDate >> Callback.future(updateMovies)
    }

    def start() = load(Tomorrow, "66")

    def render(state: FilmsState) = {
      val sortSelection = <.div(^.`class` := "menu-group",
        <.i(^.`class` := "fa fa-sort-alpha-asc fa-lg", ^.color.white),
        <.select(^.id := "ordering", ^.`class` := "menu", ^.value := state.sort.key//, ^.onChange := "TODO"
          ,
          <.option(^.value := "showtime", "Next Showing", ^.onClick --> sortBy(NextShowing)),
          <.option(^.value := "imdb", "IMDb Rating (Descending)", ^.onClick --> sortBy(ImdbRating)),
          <.option(^.value := "critic", "RT Critic Rating (Descending)", ^.onClick --> sortBy(RTCriticRating)),
          <.option(^.value := "audience", "RT Audience Rating (Descending)", ^.onClick --> sortBy(RTAudienceRating))))
      val dateSelection = <.div(^.`class` := "menu-group",
        <.i(^.`class` := "fa fa-calendar fa-lg", ^.color.white),
        <.select(^.id := "date", ^.`class` := "menu", ^.value := state.selectedDate.key, ^.onChange := "TODO",
          for (d <- state.dates) yield <.option(^.value := d.key, d.text)
        ))
      val menu = <.header(<.div(^.`class` := "menu", dateSelection, sortSelection))
      val attribution = <.div(^.id := "attribution",
        "Powered by: ",
        <.a(^.href := "http://www.cineworld.co.uk/", "Cineworld's API"), ", ",
        <.a(^.href := "http://www.omdbapi.com/", "The OMDb API"), ", ",
        <.a(^.href := "http://www.themoviedb.org/", "TheMovieDB"), " and ",
        <.a(^.href := "http://www.rottentomatoes.com/", "Rotten Tomatoes"))

      <.div( ^.id:="films",
        menu,
        FilmsList(state),
        attribution
      )
    }
  }

  val FilmPage = {
    ReactComponentB[Unit]("FilmPage")
      .initialState(FilmsState(isLoading = true, "", Map.empty, NextShowing, Today::Tomorrow::Nil, Today))
      .renderBackend[FilmsBackend]
      .componentDidMount(_.backend.start())
      .build
  }

}
