package me.gregd.cineworld.dao.imdb

import scala.util.Try
import scalaj.http.Http
import org.json4s._
import org.json4s.DefaultReaders._
import org.json4s.native.JsonMethods._
import scala.collection.mutable
import me.gregd.cineworld.Config
import org.feijoas.mango.common.cache.{CacheBuilder, LoadingCache}
import java.util.concurrent.TimeUnit._
import grizzled.slf4j.Logging
import java.text.NumberFormat

/**
 * Author: Greg Dorrell
 * Date: 11/05/2013
 */
class IMDb(rottenTomatoesApiKey:String) extends IMDbDao with Logging {
  implicit val formats = DefaultFormats

  val imdbCache = CacheBuilder.newBuilder()
      .refreshAfterWrite(1, HOURS)
      .build( imdbRatingAndVotes _ )

  val rottenTomatoesCache = CacheBuilder.newBuilder
      .refreshAfterWrite(1, HOURS)
      .build( getDetailsFromRottenTomatoes _ )

  def getId(title:String) = rottenTomatoesCache(title)._1
  def getIMDbRating(id:String) = imdbCache(id)._1
  def getVotes(id:String) = imdbCache(id)._2
  def getAudienceRating(title: String) = rottenTomatoesCache(title)._2
  def getCriticRating(title: String) = rottenTomatoesCache(title)._3


  private def getDetailsFromRottenTomatoes(title:String) = {
    val resp = parse(curl(s"http://api.rottentomatoes.com/api/public/v1.0/movies.json?page_limit=1&q=${encode(title)}&apikey=$rottenTomatoesApiKey"))
    val imdbId = (resp \ "movies" \ "alternate_ids" \ "imdb").getAs[String].map("tt"+_)
    val audienceRating = (resp \ "movies" \ "ratings" \ "audience_score").getAs[Int]
    val criticRating = (resp \ "movies" \ "ratings" \ "critics_score").getAs[Int]
    (imdbId, audienceRating, criticRating)
  }


  private def imdbRatingAndVotes(id:String): (Option[Double], Option[Int]) = {
    val resp = curl(s"http://www.omdbapi.com/?i=$id")
    val rating = Try(
      (parse(resp) \ "imdbRating").extract[String].toDouble
    ).toOption
    val votes = Try(
      (parse(resp) \ "imdbVotes").extract[String] match {
        case s => NumberFormat.getIntegerInstance.parse(s).intValue
      }
    ).toOption
    (rating,votes)
  }


  private def curl = io.Source.fromURL(_:String,"UTF-8").mkString
  private def encode = java.net.URLEncoder.encode(_:String)
}

object IMDb extends IMDb(Config.rottenTomatoesApiKey) {}

trait IMDbRating { self: {val imdbId:String} =>
  val rating = IMDb.getIMDbRating(this.imdbId)
  val votes = IMDb.getVotes(this.imdbId)
}