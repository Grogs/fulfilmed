package me.gregd.cineworld.util

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import util.Try

class JettyBootstrap {}

object JettyBootstrap extends App {
  var port = Try(System.getenv.get("PORT").toInt) getOrElse 9001
  val server = new Server( port )

  val context = new WebAppContext()
  context setContextPath "/"
  context.setResourceBase( "src/main/webapp" )
  context.addEventListener(new ScalatraListener)

  server.setHandler(context)
  server.start
  server.join
}


