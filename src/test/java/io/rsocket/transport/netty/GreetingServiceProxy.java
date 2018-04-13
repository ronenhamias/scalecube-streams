package io.rsocket.transport.netty;

import org.reactivestreams.Publisher;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.api.GreetingResponse;
import io.rsocket.transport.netty.api.GreetingService;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.publisher.Flux;

public class GreetingServiceProxy implements GreetingService {

  private RSocket socket;

  public GreetingServiceProxy() {
    socket = RSocketFactory.connect().acceptor((rSocket) -> new AbstractRSocket() {
      @Override
      public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads);
      }
    }).transport(TcpClientTransport.create("localhost", 7000)).start().block();
  }

  @Override
  public Flux<GreetingResponse> sayHellos(Publisher<GreetingRequest> requests) {
    Flux<Payload> flux = Flux.from(requests).map(req->Codec.toPayload(req));
    return socket.requestChannel(flux).map(payload->Codec.toResponse(payload));
  }
}
