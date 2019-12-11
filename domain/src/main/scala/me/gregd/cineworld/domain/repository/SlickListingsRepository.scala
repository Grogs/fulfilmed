package me.gregd.cineworld.domain.repository
import java.time.LocalDate

import cats.effect.Async
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import me.gregd.cineworld.config.ListingsTableName
import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.domain.repository.SlickListingsRepository._
import cats.syntax.functor._
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class SlickListingsRepository[F[_]: Async](db: PostgresProfile.backend.DatabaseDef, tableName: ListingsTableName) extends ListingsRepository[F] with LazyLogging {

  private val table = tableName.value

  override def fetch(cinemaId: String, date: LocalDate): F[Seq[(Movie, Seq[Performance])]] = {
    db.run(select(table, cinemaId, date)).toAsync.map(deserialize)
  }

  override def persist(cinemaId: String, date: LocalDate)(listings: Seq[(Movie, Seq[Performance])]): F[Unit] = {
    val json = listings.asJson.noSpaces
    db.run(insertOrUpdate(table, cinemaId, date, json)).toAsync.map(_ => ())
  }

  implicit class FutureToAsync[T](f: => Future[T]) {
    def toAsync: F[T] = {
      Async[F].async(cb => f.onComplete(t => cb(t.toEither))(ExecutionContext.global))
    }
  }

}

object SlickListingsRepository {
  def select(table: String, cinemaId: String, date: LocalDate) =
    sql"select listings from #$table where cinema_id = $cinemaId and date = ${date.toEpochDay.toString}"
      .as[String]
      .head

  def insertOrUpdate(table: String, cinemaId: String, date: LocalDate, json: String) =
    sqlu"insert into #$table values ($cinemaId, ${date.toEpochDay}, $json) on conflict (cinema_id, date) do update set listings = $json"

  def deserialize(json: String) = decode[Seq[(Movie, Seq[Performance])]](json).toTry.get
}
