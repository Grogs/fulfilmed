import sbt._
import Keys._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerAlias
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker

object DeployPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = noTrigger

  object autoImport {
    val deploy = taskKey[Unit]("Deploy docker image with dokku")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    deploy := {
      import scala.sys.process._

      val v = version.value
      val image = (dockerAlias in Docker).value.versioned
      val app = "fulfilmed"
      val instances = (0 to 1).toList

      val pull = s"docker pull $image"

      def stop(instance: Int) = s"docker stop $app.$instance"

      def remove(instance: Int) = s"docker rm $app.$instance"

      def create(instance: Int) = {
        val exposeTo = "900" + instance
        s"docker run -d --name $app.$instance -p $exposeTo:9000 $image"
      }

      def sleep(i: Int) = "echo 'Sleeping before app startup' && sleep 15"

      def warmup(instance: Int) =
        List(
          s"echo 'Warming up instance $instance'",
          s"curl --silent --fail --output /dev/null http://localhost:900$instance"
        ).mkString(" && ")

      def deploy(instance: Int) = List(stop(_), remove(_), create(_)).map(step => step(instance)).mkString(" && ")

      val deployAllInstances = instances.map(deploy).mkString(" && ")

      val deployCmd = s"$pull && $deployAllInstances"

      val log = streams.value.log

      log.success(s"Deploying with this cmd:\n$deployCmd")

      val status = Process("ssh", Seq("root@fulfilmed.com", deployCmd)).!

      if (status == 0) {
        log.success(s"successfully deployed $image.")
      } else {
        log.error(s"Deployment failed with status code $status.")
        throw new IllegalArgumentException("Deploy failed.")
      }
    }
  )
}
