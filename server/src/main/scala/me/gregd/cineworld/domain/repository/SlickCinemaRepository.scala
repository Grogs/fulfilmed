package me.gregd.cineworld.domain.repository

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import me.gregd.cineworld.domain.model.Cinema
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SlickCinemaRepository(db: PostgresProfile.backend.DatabaseDef) extends CinemaRepository {

  def create(): Future[Unit] = {
    def createTable = db.run(sqlu"""
           create table if not exists cinemas (
            json varchar not null,
            primary key (json)
           );
    """)

    def insertDefaultEmptyValue = db.run(sqlu"""
      insert into cinemas(json) select '' where not exists (select json from cinemas)
    """)

    createTable.flatMap(_ => insertDefaultEmptyValue).map(_ => ())
  }

  override def fetchAll(): Future[Seq[Cinema]] = {
    val select = sql"select json from cinemas".as[String]

    def deserialize(json: String) = decode[Seq[Cinema]](json).toTry.get

    db.run(
      select.head.map(deserialize)
    )
  }

  override def persist(cinemas: Seq[Cinema]): Future[Unit] = {
    {
      val json = cinemas.asJson.noSpaces
      val stmt = sqlu"update cinemas set json = $json"
      db.run(stmt).map(_ => ())
    }
  }
}
