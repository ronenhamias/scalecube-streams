package io.rsocket.transport.netty;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

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
  private Metrics metrics;

  public GreetingServiceProxy(Metrics metrics) {
    socket = RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7000)).start().block();
    this.metrics = metrics;
  }
  
  @Override
  public Flux<GreetingResponse> helloChannel(Publisher<GreetingRequest> requests) {
    return socket.requestChannel(Flux.from(requests).map(Codec::toPayload)).map(Codec::toResponse);
  }

  @Override
  public Flux<GreetingResponse> helloStream(GreetingRequest request) {
    return socket.requestStream(Codec.toPayload(request)).map(Codec::toResponse);
  }

  @Override
  public Mono<GreetingResponse> helloRequest(GreetingRequest request) {
    final Context ctx = metrics.getTimer(GreetingServiceProxy.class, "helloRequest").time();
    return socket.requestResponse(Codec.toPayload(request)).map(resp-> {
      ctx.stop();
      return Codec.toResponse(resp);
      });
  }
}
