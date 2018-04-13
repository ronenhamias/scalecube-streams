package io.rsocket.transport.netty;

import org.reactivestreams.Publisher;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class DuplexGreetingExample {

  private static final GreetingServiceImpl service = new GreetingServiceImpl();

  public static void main(String[] args) {

    // provision a service on port 7000.
    RSocketFactory.receive().acceptor((setup, reactiveSocket) -> Mono.just(new AbstractRSocket() {

      @Override
      public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        Flux<GreetingRequest> requests = Flux.from(payloads).map(message -> Codec.toRequest(message));

        return service.sayHellos(requests).map(mapper -> Codec.toPayload(mapper)); // encode back to payload.
      }
    }))

        .transport(TcpServerTransport.create("localhost", 7000)).start().subscribe();

    // interact with the service on port 7000.
    GreetingServiceProxy proxy = new GreetingServiceProxy();
    Flux<GreetingRequest> requests = Flux.just(new GreetingRequest("ronen"));// create it

    proxy.sayHellos(requests).subscribe(response -> {
      System.out.println(response);
    });

  }

}
