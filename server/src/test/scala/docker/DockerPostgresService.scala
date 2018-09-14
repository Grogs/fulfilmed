package docker

import java.net.ServerSocket
import java.sql.DriverManager

import com.typesafe.scalalogging.LazyLogging
import com.whisk.docker.{DockerCommandExecutor, DockerContainer, DockerContainerState, DockerKit, DockerReadyChecker}

import scala.concurrent.ExecutionContext

trait DockerPostgresService extends DockerKit with LazyLogging {
  import scala.concurrent.duration._

  val randomPort = {
    val socket = new ServerSocket(0)
    socket.setReuseAddress(true)
    val port = socket.getLocalPort
    socket.close()
    port
  }

  val PostgresUser = "nph"
  val PostgresPassword = "suitup"
  val postgresUrl = s"jdbc:postgresql://localhost:$randomPort/?user=$PostgresUser&password=$PostgresPassword"

  val postgresContainer = DockerContainer("postgres:10.5")
    .withPorts((randomPort, Some(randomPort)))
    .withEnv(s"POSTGRES_USER=$PostgresUser", s"POSTGRES_PASSWORD=$PostgresPassword", s"PGPORT=$randomPort")
    .withReadyChecker(PostgresReadyChecker.looped(15, 1.second))

  abstract override def dockerContainers: List[DockerContainer] =
    postgresContainer :: super.dockerContainers

  object PostgresReadyChecker extends DockerReadyChecker {

    override def apply(container: DockerContainerState)(implicit docker: DockerCommandExecutor,
                                                        ec: ExecutionContext) =
      container
        .getPorts()(docker, ec)
        .map { ports =>
          Class.forName("org.postgresql.Driver")
          val url = s"jdbc:postgresql://${docker.host}:$randomPort/"
          Option({
            val conn = DriverManager.getConnection(url, PostgresUser, PostgresPassword)
            conn.createStatement().execute("select 1")
            conn
          }).map(_.close).isDefined
        }(ec)
  }
}

