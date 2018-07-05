package me.gregd.cineworld.domain.repository

import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import me.gregd.cineworld.wiring.DatabaseConfig
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}

class SlickCinemaRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  test("create") {
    val config = DatabaseConfig("jdbc:sqlite::memory:test-creation")
    val repo = new SlickCinemaRepository(config)
    repo.create().futureValue
  }

  test("persist and fetch") {
    val config = DatabaseConfig("jdbc:sqlite::memory:test-creation")
    val repo = new SlickCinemaRepository(config)
    repo.create().futureValue

    val input = List(
      Cinema("1", "cineworld", "Blah", None),
      Cinema("2", "vue", "Blah Blah", Some(Coordinates(1d, 2d)))
    )

    repo.persist(input).futureValue

    val output = repo.fetchAll().futureValue

    input shouldEqual output
  }

}
