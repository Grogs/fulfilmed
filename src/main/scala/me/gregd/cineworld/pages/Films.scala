package me.gregd.cineworld.pages

import controllers.Assets

import scalatags.Text.all._
import scalatags.Text.tags2.{title => titleElem}

class Films {
  def apply() =
    html(
      head(
        titleElem("Fulfilmed"),
        link(rel:="stylesheet", href:="webjars/font-awesome/4.5.0/css/font-awesome.min.css"),
        link(rel:="stylesheet", href:="assets/films.css")
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
        //CONTENT GOES HERE
        div(id:="films"),
        div(id:="attribution",
          "Powered by: ", a(href:="http://www.cineworld.co.uk/", "Cineworld's API"),", ",  a(href:="http://www.omdbapi.com/","The OMDb API"),", ",  a(href:="http://www.themoviedb.org/","TheMovieDB")," and ", a(href:="http://www.rottentomatoes.com/","Rotten Tomatoes")
        )
      )
    )
}

object Films extends Films