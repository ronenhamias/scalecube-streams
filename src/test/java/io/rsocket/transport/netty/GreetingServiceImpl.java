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

  private Metrics metrics;
  private int count;

  public GreetingServiceImpl(Metrics metrics, int count) {
    // provision a service on port 7000.
    RSocketFactory.receive().acceptor(this).transport(TcpServerTransport.create("localhost", 7000)).start().subscribe();
    this.metrics = metrics;
    this.count = count;
  }

  @Override
  public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket reactiveSocket) {
    return Mono.just(new AbstractRSocket() {
      @Override
      public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return GreetingServiceImpl.this.helloChannel(Flux.from(payloads).map(Codec::toRequest)).map(Codec::toPayload);
      }

      @Override
      public Flux<Payload> requestStream(Payload payload) {
        return GreetingServiceImpl.this.helloStream(Codec.toRequest(payload)).map(Codec::toPayload);
      }

      @Override
      public Mono<Payload> requestResponse(Payload payload) {
        return GreetingServiceImpl.this.helloRequest(Codec.toRequest(payload)).map(Codec::toPayload);
      }
    });
  }

  @Override
  public Flux<GreetingResponse> helloChannel(Publisher<GreetingRequest> publisher) {
    return Flux.from(publisher).map(req ->{ 
      metrics.getCounter(GreetingServiceImpl.class, "helloChannel").inc();
      return new GreetingResponse(req.name());
          });
  }

  @Override
  public Flux<GreetingResponse> helloStream(final GreetingRequest request) {
    return Flux.range(0, count)
               .map(String::valueOf)
               .map(r->{
                 metrics.getCounter(GreetingServiceImpl.class, "helloStream").inc();
                 return new GreetingResponse("Hi there: " +request.name());
                     });
  }

  @Override
  public Mono<GreetingResponse> helloRequest(GreetingRequest request) {
    return Mono.just(new GreetingResponse("Hi there: " +request.name()));
  }
}
