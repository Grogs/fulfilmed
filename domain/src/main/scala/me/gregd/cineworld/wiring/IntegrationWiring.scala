package me.gregd.cineworld.wiring

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.softwaremill.macwire.{Module, wire}
import me.gregd.cineworld.config._
import me.gregd.cineworld.integration.PostcodeIoIntegrationService
import me.gregd.cineworld.integration.cineworld.CineworldIntegrationService
import me.gregd.cineworld.integration.omdb.OmdbIntegrationService
import me.gregd.cineworld.integration.tmdb.{ImdbIdAndAltTitles, TmdbIntegrationService, TmdbMovie}
import me.gregd.cineworld.integration.vue.VueIntegrationService
import me.gregd.cineworld.util.{Clock, FileCache}
import play.api.libs.ws.WSClient
import scalacache.{Cache, Flags}
import scalacache.serialization.Codec

import scala.concurrent.duration.Duration

@Module
class IntegrationWiring(wsClient: WSClient, cache: Cache[IO, String, Array[Byte]], clock: Clock)(vueConfig: VueConfig,
                                                                                                                cineworldConfig: CineworldConfig,
                                                                                                                omdbConfig: OmdbConfig,
                                                                                                                tmdbConfig: TmdbConfig,
                                                                                                                postcodesIoConfig: PostcodesIoConfig) {
  //todo
  class CacheAdapter[V](underlying: Cache[IO, String, Array[Byte]])(implicit codec: Codec[V, Array[Byte]]) extends Cache[IO, String, V] {
    def get(key: String)(implicit flags: Flags): IO[Option[V]] =
      underlying.get(key).map(_.traverse(codec.decode)).flatMap(IO.fromEither)
    def put(key: String)(value: V, ttl: Option[Duration])(implicit flags: Flags): IO[Unit] =
      underlying.put(key)(codec.encode(value), ttl)
    def remove(key: String): IO[Unit] =
      underlying.remove(key)
    def removeAll: IO[Unit] =
      underlying.removeAll
    def caching(key: String)(ttl: Option[Duration])(f: => V)(implicit flags: Flags): IO[V] =
      underlying.caching(key)(ttl)(codec.encode(f)).flatMap(r => IO.fromEither(codec.decode(r)))
    def cachingF(key: String)(ttl: Option[Duration])(f: IO[V])(implicit flags: Flags): IO[V] =
      underlying.cachingF(key)(ttl)(f.map(codec.encode)).flatMap(r => IO.fromEither(codec.decode(r)))
    def close: IO[Unit] =
      underlying.close
  }
  import scalacache.serialization.binary._
  val tmdbMovieCache = wire[CacheAdapter[Vector[TmdbMovie]]]
  val imdbIdCache = wire[CacheAdapter[ImdbIdAndAltTitles]]
  val stringCache = wire[CacheAdapter[String]]

  lazy val tmdbIntegrationService: TmdbIntegrationService = wire[TmdbIntegrationService]

  lazy val omdbIntegrationService: OmdbIntegrationService = wire[OmdbIntegrationService]

  lazy val postcodeIntegrationService: PostcodeIoIntegrationService = wire[PostcodeIoIntegrationService]

  lazy val cineworldIntegrationService: CineworldIntegrationService = wire[CineworldIntegrationService]

  lazy val vueIntegrationService: VueIntegrationService = wire[VueIntegrationService]
}
