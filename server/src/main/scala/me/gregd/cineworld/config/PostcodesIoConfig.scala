package me.gregd.cineworld.config
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

case class PostcodesIoConfig(baseUrl: String Refined Url)
