package me.gregd.cineworld.domain.repository

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import docker.DockerPostgresService
import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import me.gregd.cineworld.wiring.DatabaseInitialisation
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.Duration

class SlickCinemaRepositoryTest
    extends AnyFunSuite
    with ScalaFutures
    with IntegrationPatience
    with Matchers {

  val postgres = DockerPostgresService.container.map(c => Database.forURL(c.jdbcUrl, c.username, c.password))

  val _ = Class.forName("org.postgresql.Driver")

  test("persist and fetch") {
    postgres
      .use { db =>
        IO {

          db.run(DatabaseInitialisation.createCinemas).futureValue

          val repo = new SlickCinemaRepository(db)

          val input = List(
            Cinema("1", "cineworld", "Blah", None),
            Cinema("2", "vue", "Blah Blah", Some(Coordinates(1d, 2d)))
          )

          for {
            _ <- repo.persist(input)
            output <- repo.fetchAll()
          } yield input shouldEqual output
        }
      }
      .unsafeRunSync()
  }
}
