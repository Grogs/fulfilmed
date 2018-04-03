package me.gregd.cineworld

import me.gregd.cineworld.domain.Coordinates
import play.api.libs.ws.WSClient

import scala.concurrent.Future

class PostcodeService(baseUrl: String, wSClient: WSClient) {

  object model {

    case class Request(postcodes: Seq[String])

    case class Response(status: Int, result: Seq[IndividualResponse])

    case class IndividualResponse(query: String, result: Result)

    case class Result(postcode: String, latitude: Double, longitude: Double)

  }

  def lookup(postcodes: Seq[String]): Future[Map[String, Coordinates]] = {
    import model._
    wSClient
      .url("api.postcodes.io/postcodes")
      .post(Request(postcodes))
      .map { resp =>
        val res = for (IndividualResponse(_, Result(postcode, lat, long)) <- resp.json.as[Response].result) yield postcode -> Coordinates(lat, long)
        res.toMap
      }
  }
}
