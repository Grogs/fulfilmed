package me.gregd.cineworld.frontend.components.film

import autowire._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.TagMod.Composite
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, _}
import me.gregd.cineworld.domain.{CinemaApi, Movie, Performance}
import me.gregd.cineworld.frontend._
import me.gregd.cineworld.frontend.components.film
import me.gregd.cineworld.frontend.components.film.Sort.{NextShowing, Sort}
import org.scalajs.dom.document

import scala.language.implicitConversions
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalacss.ScalaCssReact.scalacssStyleaToTagMod

object FilmPageComponent {
  type Entry = (Movie, Seq[Performance])

  val sorts: Vector[Sort] = Sort.all
  val dates: Vector[Date] = Vector(Today, Tomorrow)

  def apply(props: Props) = component(props)

  case class Props(router: RouterCtl[Page], page: Films)

  case class State(
                    isLoading: Boolean,
                    cinema: String,
                    films: Map[Movie, Seq[Performance]],
                    selectedSort: Sort = NextShowing,
                    selectedDate: Date = Today
                  ) {
    def loading(date: Date): State = copy(isLoading = true, selectedDate = date)
    def loaded(films: Map[Movie, Seq[Performance]]): State = copy(isLoading = false, films = films)
    def sortBy(sort: Sort): State = copy(selectedSort = sort)
  }

  case class Date(key: String, text: String)
  object Today extends Date("today", "Today")
  object Tomorrow extends Date("tomorrow", "Tomorrow")

  val component  = {
      ScalaComponent.builder[Props]("FilmPage")
        .initialStateFromProps($ => State(isLoading = true, cinema = $.page.cinemaId, Map.empty, selectedDate = $.page.initialDate))
        .renderBackend[Backend]
        .componentWillMountConst(Callback(document.body.style.background = "#111"))
        .componentWillUnmountConst(Callback(document.body.style.background = null))
        .componentDidMount(_.backend.updateMovies)
        .build
    }


  class Backend($: BackendScope[Props, State]) {

    def updateSort(event: ReactEventFromInput) = {
      val key = event.target.value
      val sort = sorts.find(_.key == key).get
      $.modState(_.sortBy(sort))
    }

    def updateDate(event: ReactEventFromInput) = {
      val key = event.target.value
      val date = dates.find(_.key == key).get
      val updateUrl = $.props >>= (p => p.router.set(p.page.on(date)))
      updateUrl >> $.modState(_.loading(date)) >> updateMovies
    }

    def sortBy(sort: Sort) = $.modState(_.sortBy(sort))

    def updateMovies = $.state.async >>= ( state => Callback.future {
      for {
        s <- state
        movies <- Client[CinemaApi].getMoviesAndPerformances(s.cinema, s.selectedDate.key).call()
      } yield $.modState(_.loaded(movies))
    })

    def render(state: State) = {

      val sortSelection = <.div(FilmsStyle.menuGroup,
        <.i(^.`class` := "fa fa-sort-alpha-asc fa-lg", ^.color.white),
        <.select(^.id := "ordering", FilmsStyle.select, ^.value := state.selectedSort.key, ^.onChange ==> updateSort,
          Composite(for (s <- sorts)
            yield <.option(^.value := s.key, s.description))))

      val dateSelection = <.div(FilmsStyle.menuGroup,
        <.i(^.`class` := "fa fa-calendar fa-lg", ^.color.white),
        <.select(^.id := "date", FilmsStyle.select, ^.value := state.selectedDate.key, ^.onChange ==> updateDate,
          Composite(for (d <- dates) yield <.option(^.value := d.key, d.text))))

      val menu = <.header(<.div(FilmsStyle.header, dateSelection, sortSelection))

      val attribution = <.div(FilmsStyle.attribution,
        "Powered by: ",
        <.a(^.href := "http://www.omdbapi.com/", "The OMDb API"), ", ",
        <.a(^.href := "http://www.themoviedb.org/", "TheMovieDB"))

      <.div(^.id := "films", FilmsStyle.container,
        menu,
        film.FilmListComponent.FilmsList((state.isLoading, state.selectedSort, state.films)),
        attribution
      )
    }
  }


}
