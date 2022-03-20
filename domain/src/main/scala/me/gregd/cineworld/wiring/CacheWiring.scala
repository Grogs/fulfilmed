package me.gregd.cineworld.wiring

import cats.effect.IO

import java.io.File
import java.nio.file.Files
import com.softwaremill.macwire.Module
import me.gregd.cineworld.util.{FileCache, NoOpCache}
import play.api.Mode
import play.api.Mode.{Dev, Prod, Test}
import scalacache.Cache

@Module
class CacheWiring(mode: Mode) {

  lazy val cache: Cache[IO, String, Array[Byte]] = {
    val home = System.getProperty("user.home")

    def fileBasedCache(location: String) = new FileCache[Array[Byte]](location)

    mode match {
      case Test =>
        new NoOpCache
      case Dev =>
        val file = new File(s"$home/.fulfilmed-cache")
        file.mkdir()
        val path = file.toPath.toString
        fileBasedCache(path)
      case Prod =>
        val path = Files.createTempDirectory("fulfilmed-cache").toString
        fileBasedCache(path)
    }
  }

}
