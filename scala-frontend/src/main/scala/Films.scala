import scalatags.JsDom.all._
import scalatags.JsDom.tags2.{title=>titleElem}

class Films {
  def apply() =
    html(
      head(
        titleElem("Films")
      ),
      body(
        header(
          div( `class`:= "menu",
            div(`class`:="menu-group",
              i(`class`:="fa fa-calendar fa-lg", style:="color: white;"),
              select(id:="date", `class`:="menu", onchange:="TODO",
                option(value:="today", selected:="selected", "Today"),
                option(value:="tomorrow", "Tomorrow")
              )
            ),
            div(`class`:="menu-group",
              i(`class`:="fa fa-sort-alpha-asc fa-lg", style:="color: white;"),
              select(id:="ordering", `class`:="menu", onchange:="TODO",
                option(value:="?", selected:="selected", disabled, "Order by..."),
                option(value:="imdb",     "IMDb Rating (Descending)"),
                option(value:="critic",   "RT Critic Rating (Descending)"),
                option(value:="audience", "RT Audience Rating (Descending)"),
                option(value:="showtime", "Next Showing")
              )
            )
          )
        ),
        div(/*TODO*/)
      )
    )
}

object Films extends Films