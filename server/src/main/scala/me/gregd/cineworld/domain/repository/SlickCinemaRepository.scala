package me.gregd.cineworld.domain.repository

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import me.gregd.cineworld.domain.model.Cinema
import me.gregd.cineworld.wiring.DatabaseInitialisation
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SlickCinemaRepository(db: PostgresProfile.backend.DatabaseDef) extends CinemaRepository {

  def create(): Future[Unit] = {
    DatabaseInitialisation.createCinemas(db)
  }

  override def fetchAll(): Future[Seq[Cinema]] = {
    val select = sql"select json from cinemas where id = 1".as[String]

    def deserialize(json: String) = decode[Seq[Cinema]](json).toTry.get

    db.run(
      select.head.map(deserialize)
    )
  }

  override def persist(cinemas: Seq[Cinema]): Future[Unit] = {
    {
      val json = cinemas.asJson.noSpaces
      val stmt = sqlu"update cinemas set json = $json where id = 1"
      db.run(stmt).map(_ => ())
    }
  }
}
