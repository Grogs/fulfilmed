package docker

import java.sql.DriverManager
import cats.effect.{IO, Resource}
import cats.implicits.catsSyntaxFlatMapOps
import com.dimafeng.testcontainers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

import scala.concurrent.duration._

object DockerPostgresService {
  def container: Resource[IO, PostgreSQLContainer] = {
    val create = IO(PostgreSQLContainer(DockerImageName.parse("postgres:latest")))
    val acquire = create.flatMap(c => IO(c.start()).map(_ => c))
    Resource.make(acquire)(container => IO(container.stop()))
  }
}
