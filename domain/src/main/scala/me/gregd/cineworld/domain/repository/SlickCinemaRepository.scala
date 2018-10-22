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
      val upserts = DBIO.sequence(cinemas.map{c =>
        import c.{id, chain}
        sqlu"""
              insert into cinemas (id, chain, json)
              values ($id, $chain, $json)
              on conflict (id)
              do update set (id, chain, json) = ($id, $chain, $json)
        """
      })
      db.run(upserts).map(_ => ())
    }
  }
}
