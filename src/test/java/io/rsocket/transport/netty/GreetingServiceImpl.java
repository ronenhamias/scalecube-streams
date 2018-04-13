package io.rsocket.transport.netty;

import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.api.GreetingResponse;
import io.rsocket.transport.netty.api.GreetingService;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;

public class GreetingServiceImpl implements GreetingService {

  @Override
  public Flux<GreetingResponse> sayHellos(Publisher<GreetingRequest> request) {
    return Flux.from(request).map(greetingRequest -> new GreetingResponse(greetingRequest.name()));
  }

}
