package me.gregd.cineworld.frontend.components

import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, SyntheticEvent}
import japgolly.scalajs.react.vdom.prefix_<^._
import me.gregd.cineworld.domain.{CinemaApi, Movie, Performance}
import me.gregd.cineworld.frontend._
import org.scalajs.dom.html.Select
import autowire._

import scala.collection.immutable.List
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object FilmPageComponent {

  def apply() = components.FilmPage()

  object model {
    case class State(
      isLoading: Boolean,
      cinema: String,
      films: Map[Movie, List[Performance]],
      sorts: List[Sort] = List(NextShowing, ImdbRating, RTCriticRating, RTAudienceRating),
      selectedSort: Sort = NextShowing,
      dates: List[Date] = List(Today, Tomorrow),
      selectedDate: Date = Today
    )

    case class Sort(key: String, description: String, ordering: Ordering[Entry])
    object NextShowing extends Sort("showtime", "Next Showing", Ordering.by{case (_, performances) => if (performances.isEmpty) "99:99" else performances.map(_.time).min})
    object ImdbRating extends Sort("imdb", "IMDb Rating (Descending)", Ordering.by{ e: Entry => e._1.rating.getOrElse(0.0)}.reverse)
    object RTCriticRating extends Sort("critic", "RT Critic Rating (Descending)", Ordering.by{ e: Entry => e._1.criticRating.getOrElse(0)}.reverse)
    object RTAudienceRating extends Sort("audience", "RT Audience Rating (Descending)", Ordering.by{ e: Entry => e._1.audienceRating.getOrElse(0)}.reverse)

    case class Date(key: String, text: String)
    object Today extends Date("today", "Today")
    object Tomorrow extends Date("tomorrow", "Tomorrow")
  }

  object components {
    val FilmCard = ReactComponentB[(Movie, List[Performance])]("FilmCard").render_P{
      case (m, pl) =>
        <.div( ^.className := "film-container",
          <.div( ^.classSet1("film", "threedee" -> m.format.contains("3D")),
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
    }.build

    val FilmsList =
      ReactComponentB[(Boolean, model.Sort, Map[Movie, List[Performance]])]("FilmsList").render_P{
        case (loading, sort, films) =>
          def icon(faClasses: String, message: String) = {
            <.div(^.margin := "50px 0 50px 0", ^.color.white, ^.textAlign.center,
              <.i(^.`class` := s"fa $faClasses fa-5x"),
              <.div(^.`class`:="label", message)
            )
          }
          def spinner = icon("fa-refresh fa-spin", "Loading movies")
          def frown = icon("fa-frown-o", "No movies found!")
          val movies = films.toSeq.sorted(sort.ordering)
          if (movies.isEmpty)
            if (loading) spinner else frown
          else
            <.div( ^.className := "films-list-container",
              for (m <- movies) yield FilmCard(m))
      }.build

    val FilmPage = {
      ReactComponentB[Unit]("FilmPage")
        .initialState(model.State(isLoading = true, "", Map.empty))
        .renderBackend[Backend]
        .componentDidMount(_.backend.start())
        .build
    }

  }


  class Backend($: BackendScope[Unit, model.State]) {

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
      val res = Client[CinemaApi].getMoviesAndPerformances(cinema, date.key).call()
      val updateMovies = for (movies <- res)
        yield $.modState(_.copy(isLoading = false, films = movies))
      clearAndUpdate >> Callback.future(updateMovies)
    }

    def start() = load(model.Today, "66")

    def render(state: model.State) = {
      val sortSelection = <.div(^.`class` := "menu-group",
        <.i(^.`class` := "fa fa-sort-alpha-asc fa-lg", ^.color.white),
        <.select(^.id := "ordering", ^.`class` := "menu", ^.value := state.selectedSort.key, ^.onChange ==> updateSort,
          for (s <- state.sorts)
            yield <.option(^.value := s.key, s.description)))
      val dateSelection = <.div(^.`class` := "menu-group",
        <.i(^.`class` := "fa fa-calendar fa-lg", ^.color.white),
        <.select(^.id := "date", ^.`class` := "menu", ^.value := state.selectedDate.key, ^.onChange ==> updateDate,
          for (d <- state.dates)
            yield <.option(^.value := d.key, d.text)))
      val menu = <.header(<.div(^.`class` := "menu", dateSelection, sortSelection))
      val attribution = <.div(^.id := "attribution",
        "Powered by: ",
        <.a(^.href := "http://www.cineworld.co.uk/", "Cineworld's API"), ", ",
        <.a(^.href := "http://www.omdbapi.com/", "The OMDb API"), ", ",
        <.a(^.href := "http://www.themoviedb.org/", "TheMovieDB"), " and ",
        <.a(^.href := "http://www.rottentomatoes.com/", "Rotten Tomatoes"))

      <.div( ^.id:="films",
        menu,
        components.FilmsList((state.isLoading, state.selectedSort, state.films)),
        attribution
      )
    }
  }



}
