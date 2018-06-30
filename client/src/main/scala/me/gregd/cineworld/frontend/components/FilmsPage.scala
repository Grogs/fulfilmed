package me.gregd.cineworld.frontend.components

import autowire._
import me.gregd.cineworld.domain.{CinemasService, ListingsService}
import me.gregd.cineworld.frontend.Client
import me.gregd.cineworld.frontend.components.Sort.{NextShowing, Sort}
import me.gregd.cineworld.frontend.styles.FilmsStyle
import me.gregd.cineworld.frontend.util.Loadable.{Loaded, Loading, Unloaded}
import me.gregd.cineworld.frontend.util.{Loadable, Redirect, RouteProps}
import org.scalajs.dom.Event
import org.scalajs.dom.html.Input
import slinky.core.{AttrPair, Component}
import slinky.core.annotations.react
import slinky.web.html._
import autowire._
import me.gregd.cineworld.domain.model.{Movie, Performance}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.Dynamic
import org.scalajs.dom.window.console

import scala.util.{Failure, Success}

@react class FilmsPage extends Component {

  private implicit def toselectApplied(pair: AttrPair[_value_attr.type]) = pair.asInstanceOf[AttrPair[select.tag.type]]

  type Props = RouteProps

  case class State(
      films: Loadable[Map[Movie, Seq[Performance]]],
      selectedSort: Sort = NextShowing,
      redirect: Option[String] = None
  ) {
    def sortBy(sort: Sort): State = copy(selectedSort = sort)
  }

  case class Date(key: String, text: String)
  object Today extends Date("today", "Today")
  object Tomorrow extends Date("tomorrow", "Tomorrow")

  type Entry = (Movie, Seq[Performance])

  val sorts: Vector[Sort] = Sort.all
  val dates: Vector[Date] = Vector(Today, Tomorrow)

  def initialState = State(Loading)

  override def componentDidMount(): Unit = {
    console.log("Mounted")
    reloadListings()
  }

  private def reloadListings() = {
    Client[ListingsService].getMoviesAndPerformancesFor(currentCinema(), currentDate()).call().onComplete{
      case Success(movies) =>
        setState(_.copy(films = Loaded(movies)))
      case Failure(ex) =>
        val typ = ex.getClass.getSimpleName
        val msg = ex.getMessage
        console.error(s"Fetching movies failed with a $typ: $msg")
        setState(_.copy(films = Unloaded))
    }
  }

  override def componentDidUpdate(prevProps: RouteProps, prevState: State): Unit = {
    console.log("Updated")
    if (state.films == Loading) reloadListings()
  }

  def currentCinema() = props.`match`.params("cinemaId")
  def currentDate() = props.`match`.params("date")

  def updateSort(event: Event): Unit = {
    val key = event.target.asInstanceOf[Input].value
    val sort = sorts.find(_.key == key).get
    setState(_.sortBy(sort))
  }

  def updateDate(event: Event) = {
    val key = event.target.asInstanceOf[Input].value
    val date = dates.find(_.key == key).get
    setState(_.copy(films = Loading, redirect = Some(key)))
    forceUpdate()
  }

  def render() = {
    val iconStyle = style := Dynamic.literal(color = "white")
    val sortSelection =
      div(className := FilmsStyle.menuGroup)(
        i(className := "fa fa-sort-alpha-asc fa-lg", iconStyle),
        select(
          id := "ordering",
          className := FilmsStyle.select,
          value := state.selectedSort.key,
          onChange := (e => updateSort(e))
        )(
          sorts.map(
            s =>
              option(
                key := s.key,
                value := s.key,
              )(s.description))
        )
      )

    val dateSelection =
      div(className := FilmsStyle.menuGroup)(
        i(className := "fa fa-calendar fa-lg", iconStyle),
        select(id := "date",
               className := FilmsStyle.select,
               onChange := (e => updateDate(e)),
               value := currentDate(),
        )(
          for (d <- dates) yield option(key := d.key, value := d.key)(d.text)
        )
      )

    val menu = header(div(className := FilmsStyle.header)(dateSelection, sortSelection))

    val attribution =
      div(className := FilmsStyle.attribution)(
        "Powered by: ",
        a(href := "http://www.omdbapi.com/")("The OMDb API"),
        ", ",
        a(href := "http://www.themoviedb.org/")("TMDb")
      )

    state.redirect match {
      case Some(dateKey) =>
        setState(_.copy(redirect = None, films = Loading))
        Redirect(s"/films/${currentCinema()}/$dateKey")
      case None =>
        div(id := "films", className := FilmsStyle.container)(
          menu,
          FilmsList(state.films, state.selectedSort),
          attribution
        )
    }

  }
}
