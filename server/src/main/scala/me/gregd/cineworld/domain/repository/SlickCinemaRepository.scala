package me.gregd.cineworld.domain.repository

import me.gregd.cineworld.domain.model.Cinema
import me.gregd.cineworld.wiring.DatabaseConfig
import slick.jdbc.SQLiteProfile
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.Future
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

class SlickCinemaRepository(databaseConfig: DatabaseConfig) extends CinemaRepository {

  val db: SQLiteProfile.backend.DatabaseDef = Database.forURL(databaseConfig.url)

  def create(): Future[Int] = {
    db.run(sqlu"""
           create table if not exists cinemas (
            json varchar not null,
            primary key (json)
          )
    """)
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
