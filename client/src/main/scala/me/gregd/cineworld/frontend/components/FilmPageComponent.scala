package me.gregd.cineworld.frontend.components

import autowire._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, SyntheticEvent}
import me.gregd.cineworld.ServerCinemaApi
import me.gregd.cineworld.domain.{Movie, Performance}
import me.gregd.cineworld.frontend._
import org.scalajs.dom.document
import org.scalajs.dom.html.Select

import scala.collection.immutable.List
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalacss.StyleA

object FilmPageComponent {
  type Entry = (Movie, List[Performance])
  private implicit def styleaToTagMod(s: StyleA): TagMod = ^.className := s.htmlClass //TODO I get linking errors if I don't copy this across

  def apply(page: Films) = components.FilmPage(page)

  object model {
    case class State(
      isLoading: Boolean,
      cinema: String,
      films: Map[Movie, List[Performance]],
      sorts: List[Sort] = List(NextShowing, ImdbRating, TmdbRating, TmdbVotes),
      selectedSort: Sort = NextShowing,
      dates: List[Date] = List(Today, Tomorrow),
      selectedDate: Date = Today
    )

    case class Sort(key: String, description: String, ordering: Ordering[Entry])
    object NextShowing extends Sort("showtime", "Next Showing", Ordering.by{case (_, performances) => if (performances.isEmpty) "99:99" else performances.map(_.time).min})
    object ImdbRating extends Sort("imdb", "IMDb Rating (Descending)", Ordering.by{ e: Entry => e._1.rating.getOrElse(0.0)}.reverse)
    object TmdbRating extends Sort("tmdb", "TheMovieDatabase Rating (Descending)", Ordering.by{ e: Entry => e._1.tmdbRating.getOrElse(0.0)}.reverse)
    object TmdbVotes extends Sort("votes", "TheMovieDatabase Votes (Descending)", Ordering.by{ e: Entry => e._1.tmdbVotes.getOrElse(0)}.reverse)

    case class Date(key: String, text: String)
    object Today extends Date("today", "Today")
    object Tomorrow extends Date("tomorrow", "Tomorrow")
  }

  object components {
    val FilmCard = ReactComponentB[(Movie, List[Performance])]("FilmCard").render_P{
      case (m, pl) =>
        <.div( FilmsStyle.filmCard,
          <.div( FilmsStyle.filmInfo,
            <.div( ^.classSet("threedee" -> m.format.contains("3D")),
              <.div(FilmsStyle.filmTitle, m.title),
              <.div(FilmsStyle.ratings,
                <.div(FilmsStyle.imdb, <.a(^.href:=m.imdbId.map("http://www.imdb.com/title/tt" + _), m.rating)),
                <.div(FilmsStyle.rt, m.tmdbRating),
                <.div(FilmsStyle.rtAudience, m.tmdbVotes)
              ),
              <.div(FilmsStyle.times,
                for (p <- pl) yield
                  <.a(^.href := p.booking_url,
                    <.div(FilmsStyle.time, p.time)
                  )
              )
            )
          ),
          <.img( FilmsStyle.filmBackground, ^.src := m.posterUrl)
        )
    }.build

    val FilmsList =
      ReactComponentB[(Boolean, model.Sort, Map[Movie, List[Performance]])]("FilmsList").render_P{
        case (loading, sort, films) =>
          def icon(faClasses: String, message: String) = {
            <.div(^.margin := "50px 0 50px 0", ^.color.white, ^.textAlign.center,
              <.i(^.`class` := s"fa $faClasses fa-5x"),
              <.div(FilmsStyle.label, message)
            )
          }
          def spinner = icon("fa-refresh fa-spin", "Loading movies")
          def frown = icon("fa-frown-o", "No movies found!")
          val movies = films.toSeq.sorted(sort.ordering)
          if (movies.isEmpty)
            if (loading) spinner else frown
          else
            <.div( FilmsStyle.filmListContainer,
              for (m <- movies) yield FilmCard(m))
      }.build

    val FilmPage = {
      ReactComponentB[Films]("FilmPage")
        .initialState(model.State(isLoading = true, "", Map.empty))
        .renderBackend[Backend]
        .componentWillMountCB(Callback(document.body.style.background = "#111"))
        .componentWillUnmountCB(Callback(document.body.style.background = null))
        .componentDidMount( c => c.backend.load(model.Today, c.props.cinemaId))
        .build
    }

  }


  class Backend($: BackendScope[Films, model.State]) {

    def updateSort(event: SyntheticEvent[Select]) = {
      val sortKey = event.target.value
      for {
        state <- $.state
        sort = state.sorts.find(_.key == sortKey).get
        _ <- $.modState(_.copy(selectedSort = sort))
      } yield ()
    }

    def updateDate(event: SyntheticEvent[Select]) = {
      val dateKey = event.target.value
      for {
        state <- $.state
        date = state.dates.find(_.key == dateKey).get
        _ <- loadDate(date)
      } yield ()
    }

    def sortBy(sort: model.Sort) = $.modState(_.copy(selectedSort = sort))

    def loadDate(date: model.Date) = $.state.map(_.cinema) >>= (cinema => load(date, cinema))
    def loadCinema(cinema: String) = $.state.map(_.selectedDate) >>= (date => load(date, cinema))

    def load(date: model.Date, cinema: String) = {
      val clearAndUpdate = $.modState(_.copy(
        isLoading = true, films = Map.empty,
        selectedDate = date, cinema = cinema
      ))
      val res = Client[ServerCinemaApi].getMoviesAndPerformances(cinema, date.key).call()
      val updateMovies = for (movies <- res)
        yield $.modState(_.copy(isLoading = false, films = movies))
      clearAndUpdate >> Callback.future(updateMovies)
    }

    def render(state: model.State) = {
      val sortSelection = <.div(FilmsStyle.menuGroup,
        <.i(^.`class` := "fa fa-sort-alpha-asc fa-lg", ^.color.white),
        <.select(^.id := "ordering", FilmsStyle.select, ^.value := state.selectedSort.key, ^.onChange ==> updateSort,
          for (s <- state.sorts)
            yield <.option(^.value := s.key, s.description)))
      val dateSelection = <.div(FilmsStyle.menuGroup,
        <.i(^.`class` := "fa fa-calendar fa-lg", ^.color.white),
        <.select(^.id := "date", FilmsStyle.select, ^.value := state.selectedDate.key, ^.onChange ==> updateDate,
          for (d <- state.dates)
            yield <.option(^.value := d.key, d.text)))
      val menu = <.header(<.div(FilmsStyle.header, dateSelection, sortSelection))
      val attribution = <.div(FilmsStyle.attribution,
        "Powered by: ",
        <.a(^.href := "http://www.cineworld.co.uk/", "Cineworld's API"), ", ",
        <.a(^.href := "http://www.omdbapi.com/", "The OMDb API"), ", ",
        <.a(^.href := "http://www.themoviedb.org/", "TheMovieDB"), " and ",
        <.a(^.href := "http://www.rottentomatoes.com/", "Rotten Tomatoes"))

      <.div( ^.id:="films", FilmsStyle.container,
        menu,
        components.FilmsList((state.isLoading, state.selectedSort, state.films)),
        attribution
      )
    }
  }



}
