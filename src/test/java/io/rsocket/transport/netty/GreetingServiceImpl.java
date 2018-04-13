package io.rsocket.transport.netty;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.api.GreetingResponse;
import io.rsocket.transport.netty.api.GreetingService;
import io.rsocket.transport.netty.server.TcpServerTransport;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GreetingServiceImpl implements GreetingService, SocketAcceptor {

  public GreetingServiceImpl() {
    // provision a service on port 7000.
    RSocketFactory.receive().acceptor(this).transport(TcpServerTransport.create("localhost", 7000)).start().subscribe();
  }

  @Override
  public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket reactiveSocket) {
    return Mono.just(new AbstractRSocket() {
      @Override
      public Mono<Payload> requestResponse(Payload payload) {
        return GreetingServiceImpl.this.sayHello(Codec.toRequest(payload)).map(Codec::toPayload);
      }

      @Override
      public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return GreetingServiceImpl.this.sayHellos(Flux.from(payloads).map(Codec::toRequest)).map(Codec::toPayload);
      }
    });
  }

  @Override
  public Flux<GreetingResponse> sayHellos(Publisher<GreetingRequest> publisher) {
    return Flux.from(publisher).map(req -> new GreetingResponse(req.name()));
  }

  @Override
  public Mono<GreetingResponse> sayHello(GreetingRequest request) {
    return Mono.just(new GreetingResponse(request.name()));
  }
}
