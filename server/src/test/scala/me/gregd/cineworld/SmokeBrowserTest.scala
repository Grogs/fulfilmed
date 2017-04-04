package me.gregd.cineworld

import fakes.{FakeCineworldRepository, FakeRatings, FakeTheMovieDB}
import me.gregd.cineworld.dao.TheMovieDB
import me.gregd.cineworld.dao.cineworld.CineworldRepository
import me.gregd.cineworld.dao.movies.Ratings
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.selenium.HtmlUnit
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind

class SmokeBrowserTest extends FunSuite with Matchers with ScalaFutures with OneServerPerSuite with HtmlUnit {

  override lazy val app = new GuiceApplicationBuilder().overrides(bind[Ratings].toInstance(FakeRatings), bind[TheMovieDB].toInstance(FakeTheMovieDB), bind[CineworldRepository].toInstance(FakeCineworldRepository)).build

  test("Index page should list West India Quay as one or the cinemas") {
    go to s"http://localhost:$port/"
    pageTitle shouldBe "Fulfilmed"
    pageSource should include ("West India Quay")
    pageSource.lines.filter(_ contains "script").foreach(println)
  }
}
