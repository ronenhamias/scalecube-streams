package io.rsocket.transport.netty;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;

public interface GreetingService {

	Flux<GreetingResponse> sayHellos(Publisher<GreetingRequest> request);

}
