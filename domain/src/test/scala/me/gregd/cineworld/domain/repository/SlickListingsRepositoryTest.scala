package me.gregd.cineworld.domain.repository

import java.time.LocalDate

import docker.Postgres
import me.gregd.cineworld.config._
import me.gregd.cineworld.domain.model.{Movie, Performance}
import me.gregd.cineworld.wiring.DatabaseInitialisation
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import slick.jdbc
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.Duration.Inf
import scala.util.Random

class SlickListingsRepositoryTest extends FunSuite with Postgres with ScalaFutures with IntegrationPatience with Matchers {


  val exampleMovies = Seq(
    Movie("Duck Duck Goose", Some("ho00005039"), None, None, None, None, None, None, None, Some("blah.jpg"), None, None) -> List(
      Performance("10:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46903", Some("2018-04-12")),
      Performance("13:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46904", Some("2018-04-12")),
      Performance("15:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46905", Some("2018-04-12"))
    )
  )

  test("persist") {

    val repo = freshRepository()

    val eventualAssertion = for {
      _ <- repo.persist("test", LocalDate.now())(exampleMovies)
    } yield succeed

    eventualAssertion.runSyncUnsafe(Inf)
  }

  test("fetch") {

    val repo = freshRepository()

    val eventualAssertion = for {
      _ <- repo.persist("test", LocalDate.now())(exampleMovies)
      output <- repo.fetch("test", LocalDate.now())
    } yield exampleMovies shouldEqual output

    eventualAssertion.runSyncUnsafe(Inf)
  }

  private def freshRepository() = {
    type DB = jdbc.PostgresProfile.backend.DatabaseDef
    def db: DB = Database.forURL(postgresUrl)

    val tableName = ListingsTableName("listings_" + Random.alphanumeric.take(6).mkString)

    db.run(DatabaseInitialisation.createListings(tableName)).futureValue

    new SlickListingsRepository[Task](db, tableName)
  }
}
