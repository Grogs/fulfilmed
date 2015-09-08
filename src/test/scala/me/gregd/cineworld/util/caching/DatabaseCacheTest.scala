package me.gregd.cineworld.util.caching

import org.scalatest.{SequentialNestedSuiteExecution, FunSuite}
import scala.slick.driver.H2Driver.simple._
import org.scalatest.MustMatchers


/**
 * Created by Greg Dorrell on 09/06/2014.
 */
class DatabaseCacheTest extends FunSuite with MustMatchers with SequentialNestedSuiteExecution {
  val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
  val cache = new DatabaseCache[String] (
    "test",
    db,
    new String(_:Array[Byte]),
    (_:String).getBytes
  )

  test("create table") {
    DatabaseCache createIn db
  }

  test("insert") {
    cache.put("e1")(
      "Hello world!"
    )
  }

  test("retrieval") {
    val res = cache get "e1"
    res must be ('defined)
    res.get must equal ("Hello world!")
  }

}
