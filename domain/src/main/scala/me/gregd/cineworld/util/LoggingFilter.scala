package me.gregd.cineworld.util

import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import play.api.mvc._
import play.api.MarkerContext.NoMarker

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with LazyLogging {

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis
    val reqId     = requestHeader.id
    logger.info(s"servicing request $reqId: ${requestHeader.method} ${requestHeader.uri}")

    nextFilter(requestHeader).map { result =>
      val endTime     = System.currentTimeMillis
      val requestTime = endTime - startTime

      logger.info(s"serviced request $reqId: ${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}
