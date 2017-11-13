package me.gregd.cineworld.domain

case class Coordinates(lat: Double, long: Double)

case class Cinema(id: String, chain: String, name: String, coordinates: Option[Coordinates])