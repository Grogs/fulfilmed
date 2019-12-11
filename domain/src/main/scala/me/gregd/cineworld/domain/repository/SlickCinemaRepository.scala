package me.gregd.cineworld.domain.repository

import cats.effect.Async
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import me.gregd.cineworld.domain.model.Cinema
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import cats.syntax.functor._

import scala.concurrent.{ExecutionContext, Future}

class SlickCinemaRepository[F[_]: Async](db: PostgresProfile.backend.DatabaseDef) extends CinemaRepository[F] {

  override def fetchAll(): F[Seq[Cinema]] = {
    def deserialize(json: String) = decode[Seq[Cinema]](json).toTry.get
    val select                    = sql"select json from cinemas".as[String].head
    db.run(select).toAsync.map(deserialize)
  }

  override def persist(cinemas: Seq[Cinema]): F[Unit] = {
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

      db.run(upserts).toAsync.map(_ => ())
    }
  }

  implicit class FutureToAsync[T](f: => Future[T]) {
    def toAsync: F[T] = {
      Async[F].async(cb => f.onComplete(t => cb(t.toEither))(ExecutionContext.global))
    }
  }
}
