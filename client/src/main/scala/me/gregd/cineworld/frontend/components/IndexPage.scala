package me.gregd.cineworld.frontend.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.TagMod.Composite
import japgolly.scalajs.react.vdom.html_<^._
import me.gregd.cineworld.frontend.components.FilmPageComponent.model.Today
import me.gregd.cineworld.frontend.{Films, Page}

import scalacss._
//import scalacss.ScalaCssReact._

object IndexPage {
  val label = "label".reactAttr

  def apply(router: RouterCtl[Page]) = ScalaComponent.static("IndexPage", {
    def selectCinema(e: ReactEventFromInput): Callback = {
      val cinemaId = e.target.value
      router.set(Films(cinemaId, Today))
    }

    implicit def styleaToTagMod(s: StyleA): TagMod = ^.className := s.htmlClass //TODO I get linking errors if I don't copy this across
    <.div(^.id := "indexPage",
      <.div(IndexStyle.top,
        <.div(IndexStyle.title,
          "Fulfilmed"),
        <.div(IndexStyle.blurb,
          "A better way to find the best films at your local cinema")
        ,
        Composite(for {
          (typ, cinemas) <- cinemas.toVector
        } yield
          <.div(
            <.select(IndexStyle.selectWithOffset, ^.id := "cinemas", ^.`class` := ".flat", ^.onChange ==> selectCinema,
              <.option(^.value := "?", ^.selected := "selected", ^.disabled := true, typ),
              Composite(for {(groupName, cinemas) <- cinemas.toVector} yield
                <.optgroup(label := groupName,
                  Composite(for (cinema <- cinemas) yield
                    <.option(^.value := cinema.id, cinema.name)
                  ))
              ))
          )
        )),
      <.div(IndexStyle.description, ^.id := "description",
        <.h3("What?"),
        <.p("Fulfilmed lets you view the movies airing at your local cinema. It adds some features over your Cinema's standard website; inline movie ratings, sorting/filtering by rating, mark films as watched."),

        <.h3("Why?"),
        <.p("I want to easily, at a glance, figure out which film to go see at the cinema. I can't do that with my cinema's website - I have to google/research each film individually. "),

        <.h3("What's next?"),
        <.p("Currently only Cineworld and Odeon cinemas are supported; I may add more. Beyond that, I want to receive notifications about upcoming high rated films.")

      )

    )
  }
  )
  //    .componentWillMountCB(Callback(document.body.style.background = "white"))
  //    .componentWillUnmountCB(Callback(document.body.style.background = null))
  //    .build

  case class Cinema(name: String, id: String)

  val cinemas: Map[String, Map[String, Vector[Cinema]]] = Map(
    "Cineworld" -> Map(
      "London cinemas" -> Vector(
        Cinema("London - Bexleyheath", "1010810"),
        Cinema("London - Chelsea", "1010825"),
        Cinema("London - Enfield", "1010835"),
        Cinema("London - Feltham", "1010837"),
        Cinema("London - Fulham Road", "1010838"),
        Cinema("London - Haymarket", "1010848"),
        Cinema("London - Ilford", "1010852"),
        Cinema("London - Staples Corner", "1010873"),
        Cinema("London - The O2 Greenwich", "1010842"),
        Cinema("London - Wandsworth", "1010879"),
        Cinema("London - Wembley", "1010880"),
        Cinema("London - West India Quay", "1010882"),
        Cinema("London - Wood Green", "1010884")
      ),
      "All other cinemas" -> Vector(
        Cinema("Aberdeen - Queens Links", "1010804"),
        Cinema("Aberdeen - Union Square", "1010808"),
        Cinema("Aldershot", "1010805"),
        Cinema("Ashford", "1010806"),
        Cinema("Ashton-under-Lyne", "1010807"),
        Cinema("Bedford", "1010809"),
        Cinema("Birmingham - Broad Street", "1010812"),
        Cinema("Birmingham - NEC", "1010892"),
        Cinema("Boldon Tyne and Wear", "1010819"),
        Cinema("Bolton", "1010813"),
        Cinema("Bradford ", "1010811"),
        Cinema("Braintree", "1010815"),
        Cinema("Brighton", "1010816"),
        Cinema("Bristol", "1010818"),
        Cinema("Broughton", "1010889"),
        Cinema("Burton upon Trent", "1010814"),
        Cinema("Bury St Edmunds", "1010817"),
        Cinema("Cardiff", "1010821"),
        Cinema("Castleford", "1010822"),
        Cinema("Cheltenham", "1010824"),
        Cinema("Cheltenham - The Screening Rooms", "1010828"),
        Cinema("Chesterfield", "1010829"),
        Cinema("Chichester", "1010823"),
        Cinema("Crawley", "1010827"),
        Cinema("Didcot", "1010830"),
        Cinema("Didsbury", "1010831"),
        Cinema("Dundee", "1010832"),
        Cinema("Eastbourne", "1010833"),
        Cinema("Edinburgh", "1010834"),
        Cinema("Falkirk", "1010836"),
        Cinema("Glasgow - IMAX at GSC", "1010844"),
        Cinema("Glasgow - Parkhead", "1010839"),
        Cinema("Glasgow - Renfrew Street", "1010843"),
        Cinema("Glasgow - Silverburn", "1010891"),
        Cinema("Gloucester Quays", "1010841"),
        Cinema("Harlow", "1010846"),
        Cinema("Haverhill", "1010847"),
        Cinema("High Wycombe", "1010851"),
        Cinema("Hinckley", "1010895"),
        Cinema("Hull", "1010849"),
        Cinema("Huntingdon", "1010850"),
        Cinema("Ipswich", "1010854"),
        Cinema("Isle Of Wight", "1010853"),
        Cinema("Jersey", "1010855"),
        Cinema("Leigh", "1010856"),
        Cinema("Liverpool - CLOSED", "1010857"),
        Cinema("Llandudno", "1010858"),
        Cinema("Loughborough", "1010898"),
        Cinema("Luton", "1010859"),
        Cinema("Middlesbrough", "1010860"),
        Cinema("Milton Keynes", "1010861"),
        Cinema("Newport - Friars Walk", "1010893"),
        Cinema("Newport - Spytty Park", "1010862"),
        Cinema("Northampton", "1010863"),
        Cinema("Nottingham", "1010864"),
        Cinema("Rochester", "1010865"),
        Cinema("Rugby", "1010866"),
        Cinema("Runcorn", "1010867"),
        Cinema("Sheffield", "1010869"),
        Cinema("Shrewsbury", "1010870"),
        Cinema("Solihull", "1010871"),
        Cinema("Southampton", "1010872"),
        Cinema("St Helens", "1010875"),
        Cinema("St Neots", "1010887"),
        Cinema("Stevenage", "1010874"),
        Cinema("Stockport", "1010876"),
        Cinema("Stoke-on-Trent", "1010896"),
        Cinema("Swindon - Regent Circus", "1010888"),
        Cinema("Swindon - Shaw Ridge", "1010877"),
        Cinema("Telford", "1010802"),
        Cinema("Wakefield", "1010878"),
        Cinema("Weymouth", "1010881"),
        Cinema("Whiteley", "1010894"),
        Cinema("Witney", "1010883"),
        Cinema("Wolverhampton", "1010885"),
        Cinema("Yate", "1010897"),
        Cinema("Yeovil", "1010886")
      )
    )
  )

}
