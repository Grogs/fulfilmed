package me.gregd.cineworld.domain.repository
import java.time.LocalDate
import cats.effect.{Async, IO}
import cats.effect.kernel.Sync
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import me.gregd.cineworld.config.ListingsTableName
import me.gregd.cineworld.domain.model.{Movie, MovieListing, Performance}
import me.gregd.cineworld.domain.repository.SlickListingsRepository._
import cats.syntax.functor._
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class SlickListingsRepository(db: PostgresProfile.backend.DatabaseDef, tableName: ListingsTableName) extends ListingsRepository with LazyLogging {

  private val table = tableName.value

  override def fetch(cinemaId: String, date: LocalDate): IO[Seq[MovieListing]] = {
    IO.fromFuture(IO(db.run(select(table, cinemaId, date)))).map(deserialize)
  }

  override def persist(cinemaId: String, date: LocalDate)(listings: Seq[MovieListing]): IO[Unit] = {
    val json = listings.asJson.noSpaces
    IO.fromFuture(IO(db.run(insertOrUpdate(table, cinemaId, date, json)))).void
  }
}

object SlickListingsRepository {
  def select(table: String, cinemaId: String, date: LocalDate) =
    sql"select listings from #$table where cinema_id = $cinemaId and date = ${date.toEpochDay.toString}"
      .as[String]
      .head

  def insertOrUpdate(table: String, cinemaId: String, date: LocalDate, json: String) =
    sqlu"insert into #$table values ($cinemaId, ${date.toEpochDay}, $json) on conflict (cinema_id, date) do update set listings = $json"

  def deserialize(json: String) = decode[Seq[MovieListing]](json).toTry.get
}
