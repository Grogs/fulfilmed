package me.gregd.cineworld

import org.scalatest.FunSuite
import play.api.test.FakeApplication

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by grogs on 17/07/2016.
  */
class CinemaServiceIT extends FunSuite {

  implicit val ec = ExecutionContext.global

  val fakeApp = FakeApplication()

  val cinemaService = fakeApp.injector.instanceOf[CinemaService]


  test("testGetMoviesAndPerformances") {

    val res = cinemaService.getMoviesAndPerformances("1010882", "today")

    println(Await.result(res, 60.seconds))
  }

}
