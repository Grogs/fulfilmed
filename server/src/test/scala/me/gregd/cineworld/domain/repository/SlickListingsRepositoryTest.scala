package me.gregd.cineworld.domain.repository

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Cinema, Film, Movie, Performance}
import me.gregd.cineworld.wiring.DatabaseConfig
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class SlickListingsRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  test("create") {
    val config = DatabaseConfig("jdbc:sqlite::memory:test-creation")
    val repo = new SlickListingsRepository(config)
    repo.create().futureValue
  }

  test("persist and fetch") {
    val config = DatabaseConfig("jdbc:sqlite::memory:test-creation")
    val repo = new SlickListingsRepository(config)
    repo.create().futureValue

    val input = Map(
      Movie("Duck Duck Goose", Some("ho00005039"), None, None, None, None, None, None, None, Some("blah.jpg"), None, None) -> List(
        Performance("10:40", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46903", Some("2018-04-12")),
        Performance("13:10", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46904", Some("2018-04-12")),
        Performance("15:30", true, "2D", "https://www.cineworld.co.uk//ecom-tickets?tid=8112&prsntId=46905", Some("2018-04-12"))
      )
    )

    repo.persist("test", LocalDate.now())(input).futureValue

    val output = repo.fetch("test", LocalDate.now()).futureValue

    input shouldEqual output
  }
}
