package me.gregd.cineworld

import fakes.FakeRatings
import me.gregd.cineworld.config.{CineworldConfig, OmdbConfig, TmdbConfig, VueConfig}
import me.gregd.cineworld.dao.ratings.Ratings
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.selenium.HtmlUnit
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import stub.Stubs

class SmokeBrowserTest extends FunSuite with Matchers with ScalaFutures with GuiceOneServerPerSuite with HtmlUnit {

  //todo pureconfig

  override lazy val app = new GuiceApplicationBuilder()
    .overrides(
      bind[Ratings].toInstance(FakeRatings),
      bind(classOf[OmdbConfig]).toInstance(Stubs.omdb.config),
      bind(classOf[TmdbConfig]).toInstance(Stubs.tmdb.config),
      bind(classOf[CineworldConfig]).toInstance(Stubs.cineworld.config),
      bind(classOf[VueConfig]).toInstance(Stubs.vue.config),
    )
    .build

  ignore("Index page should list West India Quay as one or the cinemas") {
    go to s"http://localhost:$port/"
    pageTitle shouldBe "Fulfilmed"
    //    pageSource should include("West India Quay")
    pageSource.lines.filter(_ contains "script").foreach(println)
  }
}
