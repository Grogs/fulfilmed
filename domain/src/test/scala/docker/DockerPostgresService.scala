package docker

import java.sql.DriverManager

import cats.effect.Resource
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import net.andimiller.whales.syntax._
import net.andimiller.whales.{Binding, Docker}

import scala.concurrent.duration._

object DockerPostgresService {
  private def checkConnection(url: String) =
    Task
      .evalAsync {
        Class.forName("org.postgresql.Driver")
        val conn = DriverManager.getConnection(url)
        conn.createStatement().execute("SELECT 1")
        conn.close()
      }
      .delayExecution(200.millis)
      .onErrorRestart(maxRetries = 50)

  val postgres: Resource[Task, String] = for {
    docker <- Docker[Task]
    user = "someuser"
    pass = "somepass"
    postgres <- docker(
      "postgres",
      "11.5",
      env = Map(
        "POSTGRES_USER" -> user,
        "POSTGRES_PASSWORD" -> pass,
        "PGPORT" -> 5432.toString
      ),
      bindings = Map(5432.tcp -> Binding(hostname = Some("127.0.0.1")))
    )
    (_, port) = postgres.ports(5432.tcp).head
    url = s"jdbc:postgresql://localhost:$port/?user=$user&password=$pass"
    _ <- Resource.liftF(checkConnection(url))
  } yield url

}
