package me.gregd.cineworld.wiring

import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import slick.migration.api._
import me.gregd.cineworld.config._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseInitialisation extends LazyLogging {

  type DB = PostgresProfile.backend.DatabaseDef

  implicit val dialect: PostgresDialect = new PostgresDialect()

  def createListings(db: DB, listingsTableName: ListingsTableName): Future[Int] = db.run(sqlu"""
      create table if not exists #${listingsTableName.value} (
        cinema_id text not null,
        date text not null,
        listings text not null,
        primary key (cinema_id, date)
      )
    """)

  def createCinemas(db: DB): Future[Unit] =
    for {
      _ <- db.run(sqlu"create table if not exists cinemas (id integer, json varchar not null, primary key (id));")
      _ <- db.run(sqlu"insert into cinemas(id, json) values (1, '') on conflict do nothing;")
    } yield ()

  def migrate(db: DB, listingsTableName: ListingsTableName): Future[Unit] =
    for {
      _ <- createCinemas(db)
      _ <- createListings(db, listingsTableName)
      _ = logger.info("Database initialised.")
    } yield ()
}
