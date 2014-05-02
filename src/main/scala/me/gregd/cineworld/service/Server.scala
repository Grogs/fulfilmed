package me.gregd.cineworld.service

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.thrift.ThriftServerFramedCodec
import java.net.InetSocketAddress
import me.gregd.cineworld.domain.thrift.{CinemaService => TCS}
import org.apache.thrift.protocol.TSimpleJSONProtocol

object Server extends App {
  ServerBuilder()
    .codec(ThriftServerFramedCodec())
    .name("binary_service")
    .bindTo(new InetSocketAddress(9876))
    .build {
      new TCS.FinagledService(
        new CinemaService,
        new TSimpleJSONProtocol.Factory()
      )
    }
}
