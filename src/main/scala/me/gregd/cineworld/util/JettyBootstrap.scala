package me.gregd.cineworld.util

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyBootstrap extends App {
  var port = 9001
  val server = new Server( port )

  val context = new WebAppContext()
  context setContextPath "/"
  context.setResourceBase( "src/main/webapp" )
  context.addEventListener(new ScalatraListener)

  server.setHandler(context)
  server.start
  server.join
}


