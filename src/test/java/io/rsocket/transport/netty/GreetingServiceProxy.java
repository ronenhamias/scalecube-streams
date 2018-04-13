package io.rsocket.transport.netty;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.api.GreetingResponse;
import io.rsocket.transport.netty.api.GreetingService;
import io.rsocket.transport.netty.client.TcpClientTransport;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GreetingServiceProxy implements GreetingService {

  private RSocket socket;

  public GreetingServiceProxy() {
    socket = RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7000)).start().block();
  }

  @Override
  public Flux<GreetingResponse> sayHellos(Publisher<GreetingRequest> requests) {
    return socket.requestChannel(Flux.from(requests).map(Codec::toPayload)).map(Codec::toResponse);
  }

  @Override
  public Mono<GreetingResponse> sayHello(GreetingRequest request) {
    return socket.requestResponse(Codec.toPayload(request)).map(Codec::toResponse);
  }
}
