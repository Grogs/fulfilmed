package me.gregd.cineworld.domain.repository
import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import me.gregd.cineworld.domain.model.{Cinema, Movie, Performance}
import me.gregd.cineworld.wiring.DatabaseConfig
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import slick.jdbc.SQLiteProfile

import scala.concurrent.Future
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

class SlickListingsRepository(db: SQLiteProfile.backend.DatabaseDef) extends ListingsRepository with LazyLogging {

  override def fetch(cinemaId: String, date: LocalDate): Future[Seq[(Movie, Seq[Performance])]] = {
    val select = sql"select listings from listings where cinema_id = $cinemaId and date = ${date.toEpochDay}".as[String]

    def deserialize(json: String) = decode[Seq[(Movie, Seq[Performance])]](json).toTry.get

    db.run(
      select.head.map(deserialize)
    )
  }

  override def persist(cinemaId: String, date: LocalDate)(listings: Seq[(Movie, Seq[Performance])]): Future[Unit] = {
    val json = listings.asJson.noSpaces
    val stmt = sqlu"insert or replace into listings values ($cinemaId, ${date.toEpochDay}, $json)"
    db.run(stmt).map(_ => ())
  }

  def create(): Future[Int] = {
    db.run(sqlu"""
           create table if not exists listings (
            cinema_id varchar not null,
            date varchar not null,
            listings text not null,
            primary key (cinema_id, date)
          )
    """)
  }
}
