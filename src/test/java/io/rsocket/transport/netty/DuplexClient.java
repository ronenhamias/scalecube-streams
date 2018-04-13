package io.rsocket.transport.netty;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import java.time.Duration;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class DuplexClient {

  public static void main(String[] args) {
    RSocketFactory.receive()
        .acceptor(
            (setup, reactiveSocket) -> {
              reactiveSocket.requestChannel(payloads->{
                GreetingService.sayHello(payloads);
                
              });
              
              return Mono.just(new AbstractRSocket() {});
            })
        .transport(TcpServerTransport.create("localhost", 7000))
        .start()
        .subscribe();

    
    
    RSocket socket =
        RSocketFactory.connect()
            .acceptor(
                rSocket -> new AbstractRSocket() {
                  @Override
                  public Flux<Payload> requestChannel(Publisher<Payload> payloads) {

                    return Flux.from(payloads);

                  }
                })
            .transport(TcpClientTransport.create("localhost", 7000))
            .start()
            .block();
    
    
    for(int i =0; i<100000000;i++)
      socket.requestStream(DefaultPayload.create("Bi-di Response => " + i));
    
    socket.onClose().block();
  }
}
