import cats.effect.{IO, IOApp}
import slinky.reactrouter._
import slinky.web.html._
import me.gregd.cineworld.frontend.components.{FilmsPage, IndexPage}
import org.scalajs.dom.{document, window}
import slinky.core.ReactComponentClass
import slinky.web.ReactDOM
import slinky.history.History

object Main extends IOApp.Simple {

  def run: IO[Unit] = {

    IO{
      ReactDOM.render(
        Router(History.createBrowserHistory())(
          Switch(
            Route("/", IndexPage, exact = true),
            Route("/index", IndexPage, exact = true),
            Route("/films/:cinemaId/:date", FilmsPage, exact = true),
          )
        ),
        document.getElementById("content")
      )
    } >> IO.never

  }

}
