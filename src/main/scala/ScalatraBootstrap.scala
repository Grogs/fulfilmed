import me.gregd.cineworld.Config
import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import me.gregd.cineworld.rest.CinemaService

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context mount (Config.webservice, "/api/*")
  }
}
