package me.gregd.cineworld.domain.repository

import docker.Postgres
import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import me.gregd.cineworld.wiring.DatabaseInitialisation
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.Duration

class SlickCinemaRepositoryTest extends FunSuite with Postgres with ScalaFutures with IntegrationPatience with Matchers {

  lazy val db = Database.forURL(postgresUrl)

  test("persist and fetch") {
    db.run(DatabaseInitialisation.createCinemas).futureValue
    val repo = new SlickCinemaRepository[Task](db)

    val input = List(
      Cinema("1", "cineworld", "Blah", None),
      Cinema("2", "vue", "Blah Blah", Some(Coordinates(1d, 2d)))
    )

    val eventualAssertion = for {
      _ <- repo.persist(input)
      output <- repo.fetchAll()
    } yield input shouldEqual output

    eventualAssertion.runSyncUnsafe(Duration.Inf)(Scheduler.global, implicitly)
  }
}
