package me.gregd.cineworld.integration.vue

import org.scalatest.{FunSuite, Matchers}

class ImageUrlTest extends FunSuite with Matchers {

  test("vue hosted image placeholder") {
    val res = ImageUrl.resolve("/-/media/images/vue holding images/holdingimage_poster_550x820.jpg")
    res shouldBe "https://www.myvue.com/-/media/images/vue holding images/holdingimage_poster_550x820.jpg"
  }

  test("vue hosted image") {
    val res = ImageUrl.resolve("/-/media/images/film%20and%20events/may%202017/johnlecarreposter.jpg")
    res shouldBe "https://www.myvue.com/-/media/images/film%20and%20events/may%202017/johnlecarreposter.jpg"
  }

  test("external image") {
    val res = ImageUrl.resolve("//images.mymovies.net/images/film/cin/350x522/fid17620.jpg")
    res shouldBe "http://images.mymovies.net/images/film/cin/350x522/fid17620.jpg"
  }

}
