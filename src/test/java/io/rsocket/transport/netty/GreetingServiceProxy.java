package io.rsocket.transport.netty;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.api.GreetingResponse;
import io.rsocket.transport.netty.api.GreetingService;
import io.rsocket.transport.netty.client.TcpClientTransport;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;

public class GreetingServiceProxy implements GreetingService {

  private RSocket socket;

  public GreetingServiceProxy() {
    socket = RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7000)).start().block();
  }

  @Override
  public Flux<GreetingResponse> helloChannel(Publisher<GreetingRequest> requests) {
    return socket.requestChannel(Flux.from(requests).map(Codec::toPayload)).map(Codec::toResponse);
  }

  @Override
  public Flux<GreetingResponse> helloStream(GreetingRequest request) {
    return socket.requestStream(Codec.toPayload(request)).map(Codec::toResponse);
  }
}
