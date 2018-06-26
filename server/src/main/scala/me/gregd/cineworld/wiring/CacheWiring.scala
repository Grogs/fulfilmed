package me.gregd.cineworld.wiring

import java.io.File
import java.nio.file.Files

import me.gregd.cineworld.util.{FileCache, NoOpCache}
import play.api.Mode
import play.api.Mode.{Dev, Prod, Test}

import scalacache.ScalaCache

class CacheWiring(mode: Mode) {

  lazy val cache: ScalaCache[Array[Byte]] = {
    val home = System.getProperty("user.home")
    val tmp = System.getProperty("java.io.tmpdir")

    def fileBasedCache(location: String) = ScalaCache(new FileCache(location))

    mode match {
      case Test =>
        NoOpCache.cache
      case Dev =>
        val file = new File(s"$home/.fulmfilmed-cache")
        file.mkdir()
        val path = file.toPath.toString
        fileBasedCache(path)
      case Prod =>
        val path = Files.createTempDirectory("fulfilmed-cache").toString
        fileBasedCache(path)

    }
  }

}
