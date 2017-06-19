package me.gregd.cineworld

import fakes.FakeRatings
import me.gregd.cineworld.config.values.{CineworldUrl, TmdbUrl}
import me.gregd.cineworld.dao.ratings.Ratings
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.selenium.HtmlUnit
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneServerPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import stub.Stubs

class SmokeBrowserTest extends FunSuite with Matchers with ScalaFutures with OneServerPerSuite with HtmlUnit {

  override lazy val app = new GuiceApplicationBuilder()
    .overrides(
      bind[Ratings].toInstance(FakeRatings),
      bind[TmdbUrl].toInstance(Stubs.tmdb.baseUrl),
      bind[CineworldUrl].toInstance(Stubs.cineworld.baseUrl)
    )
    .build

  test("Index page should list West India Quay as one or the cinemas") {
    go to s"http://localhost:$port/"
    pageTitle shouldBe "Fulfilmed"
//    pageSource should include("West India Quay")
    pageSource.lines.filter(_ contains "script").foreach(println)
  }
}
