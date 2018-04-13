package io.scalecube.streams.rsocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.ByteBufPayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReqStreamTickingSocketAcceptor implements SocketAcceptor {
  private static Logger log = LoggerFactory.getLogger(ReqStreamTickingSocketAcceptor.class);

  @Override
  public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
    return Mono.just(new AbstractRSocket() {
      @Override
      public Flux<Payload> requestStream(Payload payload) {
        log.info("Server rcvd: {}", payload.getDataUtf8());
        return Flux.interval(Duration.ofSeconds(1)).take(5).map(l -> ByteBufPayload.create("Tick:" + l));
      }
    });
  }
}
