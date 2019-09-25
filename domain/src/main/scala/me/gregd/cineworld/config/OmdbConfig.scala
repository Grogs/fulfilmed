package me.gregd.cineworld.config
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

case class OmdbConfig(baseUrl: String Refined Url, apiKey: String)
