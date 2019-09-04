package me.gregd.cineworld.domain.repository

import java.time.LocalDate

import docker.DockerPostgresService
import me.gregd.cineworld.config._
import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.wiring.DatabaseInitialisation
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{AsyncFunSuite, Matchers, ParallelTestExecution}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.util.Random

class SlickListingsRepositoryTest
    extends AsyncFunSuite
    with ScalaFutures
    with IntegrationPatience
    with ParallelTestExecution
    with Matchers {

  val postgres = DockerPostgresService.postgres.map(Database.forURL(_))

  val exampleMovies = Seq(
    Movie(
      "Duck Duck Goose",
      Some("ho00005039"),
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      Some("blah.jpg"),
      None,
      None
    ) -> List(
      Performance(
        "10:40",
        available = true,
        "2D",
        "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46903",
        Some("2018-04-12")
      ),
      Performance(
        "13:10",
        available = true,
        "2D",
        "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46904",
        Some("2018-04-12")
      ),
      Performance(
        "15:30",
        available = true,
        "2D",
        "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46905",
        Some("2018-04-12")
      )
    )
  )

  test("persist") {
    postgres.use { db =>
      val repo = freshRepository(db)

      for {
        _ <- repo.persist("test", LocalDate.now())(exampleMovies)
      } yield succeed
    }.runToFuture
  }

  test("fetch") {
    postgres.use { db =>
      val repo = freshRepository(db)

      for {
        _ <- repo.persist("test", LocalDate.now())(exampleMovies)
        output <- repo.fetch("test", LocalDate.now())
      } yield exampleMovies shouldEqual output
    }.runToFuture
  }

  def freshRepository(db: PostgresProfile.backend.DatabaseDef) = {
    val tableName = ListingsTableName(
      "listings_" + Random.alphanumeric.take(6).mkString
    )
    db.run(DatabaseInitialisation.createListings(tableName)).futureValue
    new SlickListingsRepository[Task](db, tableName)
  }
}
