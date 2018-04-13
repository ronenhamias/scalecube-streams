package io.rsocket.transport.netty;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class DuplexGreetingExample {

  private static final GreetingServiceImpl service = new GreetingServiceImpl();

  public static void main(String[] args) {
    RSocketFactory.receive().acceptor(
        (setup, reactiveSocket) -> Mono.just(new AbstractRSocket() {
              
          @Override
              public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                Flux<GreetingRequest> requests = Flux.from(payloads)
                    .map(message->decode(message));

                return Flux.from(service.sayHello(requests))
                    .map(mapper->encode(mapper)); // encode back to payload.
              }
            }))
    
        .transport(TcpServerTransport.create("localhost", 7000))
        .start()
        .subscribe();
    
    GreetingServiceProxy proxy = new GreetingServiceProxy();
    
	Flux<GreetingRequest> requests = null;// create it 
    proxy.sayHello(requests);
    
  }
  
  protected static Payload encode(GreetingResponse mapper) {
    return null;
  }

  private static GreetingRequest decode(Payload message) {
    return new GreetingRequest();
  }
}
