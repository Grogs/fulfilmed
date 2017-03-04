package me.gregd.cineworld

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.selenium.HtmlUnit
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.OneServerPerSuite


class SmokeBrowserTest extends FunSuite with Matchers with ScalaFutures with OneServerPerSuite with HtmlUnit {


  test("Index page should list West India Quay as one or the cinemas") {
    go to s"http://localhost:$port/"
    pageTitle shouldBe "Test Page"
//    click on find(name("b")).get
//    eventually { pageTitle shouldBe "scalatest" }
  }
}
