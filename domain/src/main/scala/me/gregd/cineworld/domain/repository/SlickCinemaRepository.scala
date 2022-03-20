package me.gregd.cineworld.domain.repository

import cats.effect.{Async, IO, Sync}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import me.gregd.cineworld.domain.model.Cinema
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import cats.syntax.functor._

import scala.concurrent.{ExecutionContext, Future}

class SlickCinemaRepository(db: PostgresProfile.backend.DatabaseDef) extends CinemaRepository {
  override def fetchAll(): IO[Seq[Cinema]] = {
    def deserialize(json: String) = decode[Seq[Cinema]](json).toTry.get
    val select                    = sql"select json from cinemas".as[String].head
    IO.fromFuture(IO(
      db.run(select)
    )).map(deserialize)
  }

  override def persist(cinemas: Seq[Cinema]): IO[Unit] = {
    {
      val json = cinemas.asJson.noSpaces
      val upserts = DBIO.sequence(cinemas.map { c =>
        import c.{chain, id}
        sqlu"""
              insert into cinemas (id, chain, json)
              values ($id, $chain, $json)
              on conflict (id)
              do update set (id, chain, json) = ($id, $chain, $json)
        """
      })

      IO.fromFuture(IO(
        db.run(upserts)
      )).void
    }
  }
}
