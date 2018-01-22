package me.gregd.cineworld.wiring

import java.io.File
import java.nio.file.Files

import me.gregd.cineworld.Cache
import me.gregd.cineworld.util.FileCache
import play.api.Environment
import play.api.Mode.Dev

import scalacache.ScalaCache

trait FileCacheWiring {

  def environment: Environment

  lazy val cache: Cache = {
    val home = System.getProperty("user.home")
    val tmp = System.getProperty("java.io.tmpdir")
    val cacheLocation = environment.mode match {
      case Dev =>
        val res = new File(s"$home/.fulmfilmed-cache")
        res.mkdir()
        res.toPath.toString
      case _ =>
        Files.createTempDirectory("fulfilmed-cache").toString

    }

    Cache(ScalaCache(new FileCache(cacheLocation)))
  }

}
