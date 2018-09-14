package me.gregd.cineworld.domain.repository
import java.time.LocalDate

import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.wiring.ListingsTableName
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SlickListingsRepository(db: PostgresProfile.backend.DatabaseDef, tableName: ListingsTableName) extends ListingsRepository with LazyLogging {

  override def fetch(cinemaId: String, date: LocalDate): Future[Seq[(Movie, Seq[Performance])]] = {
    val select = sql"select listings from ${tableName.value} where cinema_id = $cinemaId and date = ${date.toEpochDay}".as[String]

    def deserialize(json: String) = decode[Seq[(Movie, Seq[Performance])]](json).toTry.get

    db.run(
      select.head.map(deserialize)
    )
  }

  override def persist(cinemaId: String, date: LocalDate)(listings: Seq[(Movie, Seq[Performance])]): Future[Unit] = {
    val json = listings.asJson.noSpaces
    val stmt = sqlu"insert into ${tableName.value} values ('$cinemaId', '${date.toEpochDay}', '$json') on conflict do nothing"
    db.run(stmt).map(_ => ())
  }

  def create(): Future[Int] = {
    db.run(sqlu"""
           create table if not exists ${tableName.value} (
            cinema_id varchar not null,
            date varchar not null,
            listings text not null,
            primary key (cinema_id, date)
          )
    """)
  }
}
