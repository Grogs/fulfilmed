package me.gregd.cineworld.domain.repository

import java.time.LocalDate

import me.gregd.cineworld.domain.model.{Movie, Performance}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import slick.jdbc.SQLiteProfile.api._

class SlickListingsRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  test("create") {
    val db = Database.forURL("jdbc:sqlite::memory:SlickCinemaRepositoryTest")
    val repo = new SlickListingsRepository(db)
    repo.create().futureValue
  }

  test("persist and fetch") {
    val db = Database.forURL("jdbc:sqlite::memory:SlickCinemaRepositoryTest")
    val repo = new SlickListingsRepository(db)
    repo.create().futureValue

    val input = Seq(
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
