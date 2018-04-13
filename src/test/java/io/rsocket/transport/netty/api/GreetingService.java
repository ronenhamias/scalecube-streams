package io.rsocket.transport.netty.api;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GreetingService {

  Flux<GreetingResponse> helloChannel(Publisher<GreetingRequest> request);

  Flux<GreetingResponse> helloStream(GreetingRequest request);

  Mono<GreetingResponse> helloRequest(GreetingRequest request);
}
