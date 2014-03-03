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

class Ratings(rottenTomatoesApiKey:String) extends IMDbDao with Logging {
  implicit val formats = DefaultFormats

  val imdbCache = CacheBuilder.newBuilder()
    .refreshAfterWrite(3, HOURS)
    .build( imdbRatingAndVotes _ )

  val rottenTomatoesCache = CacheBuilder.newBuilder
    .refreshAfterWrite(4, HOURS)
    .build( getDetailsFromRottenTomatoes _ )

  def getId(title:String) = rottenTomatoesCache(title)._1
  def getIMDbRating(id:String) = imdbCache(id)._1
  def getVotes(id:String) = imdbCache(id)._2
  def getAudienceRating(title: String) = rottenTomatoesCache(title)._2
  def getCriticRating(title: String) = rottenTomatoesCache(title)._3


  private def getDetailsFromRottenTomatoes(title:String) = {
    logger.debug(s"Retreiving IMDb ID and Rotten Tomatoes ratings for '$title'")
    val resp = parse(curl(s"http://api.rottentomatoes.com/api/public/v1.0/movies.json?page_limit=1&q=${encode(title)}&apikey=$rottenTomatoesApiKey"))
    logger.debug(s"Rotten Tomatoes response for $title:\n$resp")

    val imdbId = (resp \ "movies" \ "alternate_ids" \ "imdb").getAs[String].map("tt"+_)
    val audienceRating = (resp \ "movies" \ "ratings" \ "audience_score").getAs[Int]
    val criticRating = (resp \ "movies" \ "ratings" \ "critics_score").getAs[Int]
    logger.debug(s"$title: $imdbId, $audienceRating, $criticRating")

    (imdbId, audienceRating, criticRating)
  }


  private def imdbRatingAndVotes(id:String): (Option[Double], Option[Int]) = {
    logger.debug(s"Retreiving IMDb rating and votes for $id")
    val resp = curl(s"http://www.omdbapi.com/?i=$id")
    logger.debug(s"IMDb response for $id:\n$resp")
    val rating = Try(
      (parse(resp) \ "imdbRating").extract[String].toDouble
    ).toOption
    val votes = Try(
      (parse(resp) \ "imdbVotes").extract[String] match { //needed as ',' is used as decimal mark
        case s => NumberFormat.getIntegerInstance.parse(s).intValue
      }
    ).toOption
    logger.debug(s"$id: $rating with $votes votes")
    (rating,votes)
  }


  private def curl = io.Source.fromURL(_:String,"UTF-8").mkString
  private def encode = java.net.URLEncoder.encode(_:String,"UTF-8")
}

object Ratings extends Ratings(Config.rottenTomatoesApiKey) {}

trait IMDbRating { self: {val imdbId:String} =>
  val rating = Ratings.getIMDbRating(this.imdbId)
  val votes = Ratings.getVotes(this.imdbId)
}