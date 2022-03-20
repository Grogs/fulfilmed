package me.gregd.cineworld.integration

import cats.effect.IO
import me.gregd.cineworld.domain.model.Coordinates
import me.gregd.cineworld.config.PostcodesIoConfig
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostcodeIoIntegrationService(config: PostcodesIoConfig, wSClient: WSClient) {

  object model {

    case class Request(postcodes: Seq[String])

    case class Result(postcode: String, latitude: Option[Double], longitude: Option[Double])
    case class Response(query: String, result: Option[Result])
    case class BulkResponse(status: Int, result: Seq[Response])

    implicit val a = Json.format[Request]
    implicit val d = Json.format[Result]
    implicit val c = Json.format[Response]
    implicit val b = Json.format[BulkResponse]
  }

  def lookup(postcodes: Seq[String]): IO[Map[String, Coordinates]] = {
    import model._
    IO.fromFuture(
        IO(
          wSClient
            .url(s"${config.baseUrl}/postcodes")
            .post(Json.toJson(Request(postcodes)))))
      .map { resp =>
        val res =
          for {
            Response(_, Some(Result(postcode, lat, long))) <- resp.json.as[BulkResponse].result
            latitude                                       <- lat
            longitude                                      <- long
          } yield postcode -> Coordinates(latitude, longitude)
        res.toMap
      }
  }
}
