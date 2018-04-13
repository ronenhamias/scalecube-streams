package io.rsocket.transport.netty;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;

public class GreetingServiceImpl implements GreetingService {

	@Override
	public Flux<GreetingResponse> sayHellos(Publisher<GreetingRequest> request) {
		return null;
	}

}
