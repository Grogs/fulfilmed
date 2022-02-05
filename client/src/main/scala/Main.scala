import me.gregd.cineworld.frontend.util.{History, Route, Router, Switch}
import me.gregd.cineworld.frontend.components.{IndexPage, FilmsPage}
import org.scalajs.dom.document
import slinky.web.ReactDOM

object Main {

  def main(args: Array[String]): Unit = {

    ReactDOM.render(
      Router(History.createBrowserHistory())(
        Switch(
          Route("/", IndexPage, exact = Some(true)),
          Route("/index", IndexPage, exact = Some(true)),
          Route("/films/:cinemaId/:date", FilmsPage, exact = Some(true)),
        )
      ),
      document.getElementById("content")
    )

  }

}
