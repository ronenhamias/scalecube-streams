package io.rsocket.transport.netty.api;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GreetingService {

  Flux<GreetingResponse> sayHellos(Publisher<GreetingRequest> request);

  Mono<GreetingResponse> sayHello(GreetingRequest request);
}
