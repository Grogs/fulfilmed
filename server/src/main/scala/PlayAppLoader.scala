import me.gregd.cineworld.PlayWiring
import me.gregd.cineworld.wiring._
import play.api.{Application, ApplicationLoader}

class PlayAppLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context): Application = new PlayWiring(context).application
}


