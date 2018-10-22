package me.gregd.cineworld.config

case class DatabaseConfig(url: String, listingsTableName: ListingsTableName, username: Option[String], password: Option[String])
