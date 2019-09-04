package me.gregd.cineworld.integration.vue

object ImageUrl {

  def resolve(vueImageUrl: String): String = {
    if (vueImageUrl startsWith "//")
      s"http:$vueImageUrl"
    else
      s"https://www.myvue.com$vueImageUrl"
  }

}
