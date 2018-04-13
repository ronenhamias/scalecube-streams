package io.rsocket.transport.netty;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class DuplexGreetingExample {

  private static final GreetingService service = new GreetingService();

  public static void main(String[] args) {
    RSocketFactory.receive().acceptor(
        (setup, reactiveSocket) -> Mono.just(new AbstractRSocket() {
              
          @Override
              public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                Flux<GreetingRequest> requests = Flux.from(payloads)
                    .map(message->decode(message));
                
                Subscriber<GreetingRequest> sub = null; // create subscriber?;
                requests.subscribe(actual->{
                  sub.onNext(actual);
                });
                
                return Flux.from(service.sayHello(sub)).map(mapper->encode(mapper));
              }
            }))
    
        .transport(TcpServerTransport.create("localhost", 7000))
        .start()
        .subscribe();


    
    
    RSocket socket = RSocketFactory.connect().acceptor(rSocket -> new AbstractRSocket() {
      @Override
      public Flux<Payload> requestChannel(Publisher<Payload> payloads) {

        return Flux.from(payloads);

      }
    }).transport(TcpClientTransport.create("localhost", 7000)).start().block();


    socket.onClose().block();
  }
  
  protected static Payload encode(GreetingResponse mapper) {
    return null;
  }

  private static GreetingRequest decode(Payload message) {
    return new GreetingRequest();
  }
}
