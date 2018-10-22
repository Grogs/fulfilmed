package docker
import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.Suite

trait Postgres extends DockerKitDockerJava with DockerPostgresService with DockerTestKit { self: Suite => }
