package me.gregd.cineworld.domain.repository

import me.gregd.cineworld.domain.model.{Cinema, Coordinates}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FunSuite, Matchers}
import slick.jdbc.SQLiteProfile.api._

class SlickCinemaRepositoryTest extends FunSuite with ScalaFutures with IntegrationPatience with Matchers {

  val db = Database.forURL("jdbc:sqlite::memory:SlickCinemaRepositoryTest")
  val repo = new SlickCinemaRepository(db)

  test("create") {
    repo.create().futureValue
  }

  test("persist and fetch") {
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
