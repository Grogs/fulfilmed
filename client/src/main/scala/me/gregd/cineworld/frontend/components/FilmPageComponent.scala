package me.gregd.cineworld.frontend.components

import autowire._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.TagMod.Composite
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, _}
import me.gregd.cineworld.ServerCinemaApi
import me.gregd.cineworld.domain.{Movie, Performance}
import me.gregd.cineworld.frontend._
import me.gregd.cineworld.frontend.components.FilmPageComponent.model._
import org.scalajs.dom.document

import scala.language.implicitConversions
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalacss.StyleA

object FilmPageComponent {
  type Entry = (Movie, Seq[Performance])

  val sorts: Vector[Sort] = Vector(NextShowing, ImdbRating, TmdbRating, TmdbVotes)
  val dates: Vector[Date] = Vector(Today, Tomorrow)

  private implicit def styleaToTagMod(s: StyleA): TagMod = ^.className := s.htmlClass //TODO I get linking errors if I don't copy this across

  def apply(props: Props) = components.FilmPage(props)

  object model {

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

    case class Sort(key: String, description: String, ordering: Ordering[Entry])

    object NextShowing extends Sort("showtime", "Next Showing", Ordering.by { case (_, performances) => if (performances.isEmpty) "99:99" else performances.map(_.time).min })

    object ImdbRating extends Sort("imdb", "IMDb Rating (Descending)", Ordering.by { e: Entry => e._1.rating.getOrElse(0.0) }.reverse)

    object TmdbRating extends Sort("tmdb", "TheMovieDatabase Rating (Descending)", Ordering.by { e: Entry => e._1.tmdbRating.getOrElse(0.0) }.reverse)

    object TmdbVotes extends Sort("votes", "TheMovieDatabase Votes (Descending)", Ordering.by { e: Entry => e._1.tmdbVotes.getOrElse(0) }.reverse)

    case class Date(key: String, text: String)

    object Today extends Date("today", "Today")

    object Tomorrow extends Date("tomorrow", "Tomorrow")

  }

  object components {
    val filmCard = (m: Movie, pl: Seq[Performance]) =>
      <.div(FilmsStyle.filmCard,
        <.div(FilmsStyle.filmInfo,
          <.div(^.classSet("threedee" -> m.format.contains("3D")),
            <.div(FilmsStyle.filmTitle, m.title),
            <.div(FilmsStyle.ratings, imdbLink(m), tmdbLink(m)),
            <.div(FilmsStyle.times,
              Composite(for (p <- pl.toVector) yield
                <.a(^.href := p.booking_url,
                  <.div(FilmsStyle.time, p.time)
                )
              ))
          )
        ),
        <.img(FilmsStyle.filmBackground, ^.src := m.posterUrl.get)
      )

    def tmdbLink(m: Movie) = m.tmdbRating.whenDefined(<.div(FilmsStyle.tmdb, _))

    def imdbLink(m: Movie) = {
      for {
        rating <- m.rating
        id <- m.imdbId
      } yield <.div(FilmsStyle.imdb, <.a(^.href := s"http://www.imdb.com/title/tt$id", rating))
    }.whenDefined

    val FilmsList =
      ScalaComponent.builder[(Boolean, Sort, Map[Movie, Seq[Performance]])]("FilmsList").render_P {
        case (loading, sort, films) =>
          def icon(faClasses: String, message: String) = {
            <.div(^.margin := "50px 0 50px 0", ^.color.white, ^.textAlign.center,
              <.i(^.`class` := s"fa $faClasses fa-5x"),
              <.div(FilmsStyle.label, message)
            )
          }

          val spinner = icon("fa-refresh fa-spin", "Loading movies")

          val frown = icon("fa-frown-o", "No movies found!")

          val movies = films.toVector.sorted(sort.ordering)
          if (movies.isEmpty)
            if (loading) spinner else frown
          else
            <.div(
              FilmsStyle.filmListContainer,
              Composite(for (m <- movies) yield filmCard.tupled(m))
            )
      }.build

    val FilmPage = {
      ScalaComponent.builder[Props]("FilmPage")
        .initialStateFromProps($ => State(isLoading = true, cinema = $.page.cinemaId, Map.empty, selectedDate = $.page.initialDate))
        .renderBackend[Backend]
        .componentWillMountConst(Callback(document.body.style.background = "#111"))
        .componentWillUnmountConst(Callback(document.body.style.background = null))
        .componentDidMount(_.backend.updateMovies)
        .build
    }
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
        movies <- Client[ServerCinemaApi].getMoviesAndPerformances(s.cinema, s.selectedDate.key).call()
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
        components.FilmsList((state.isLoading, state.selectedSort, state.films)),
        attribution
      )
    }
  }


}
