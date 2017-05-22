package me.gregd.cineworld.dao.cinema.vue.raw

import org.scalatest.matchers.{BeMatcher, MatchResult}
import play.api.libs.json.Json

import scala.util.Try

trait JsonMatchers {

  val validJson =
    BeMatcher { (left: String) =>
      MatchResult(
        Try(Json.parse(left)).isSuccess,
        s"Was not valid json:\n${left.take(100)}...",
        s"Was valid json:\n${left.take(100)}...")
    }

}
