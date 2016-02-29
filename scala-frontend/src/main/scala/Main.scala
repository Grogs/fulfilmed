import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._

object Main extends JSApp {
  @JSExport
  override def main(): Unit = {
    println("HELLO")
    alert("HELLO")
  }

}