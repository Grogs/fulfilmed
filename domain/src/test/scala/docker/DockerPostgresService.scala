package docker

import java.sql.DriverManager

import cats.effect.Resource
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import com.dimafeng.testcontainers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

import scala.concurrent.duration._

object DockerPostgresService {
  def container: Resource[Task, PostgreSQLContainer] = {
    val create = Task(PostgreSQLContainer(DockerImageName.parse("postgres:latest")))
    val acquire = create.flatMap(c => Task(c.start()).map(_ => c))
    Resource.make(acquire)(container => Task(container.stop()))
  }

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
}
