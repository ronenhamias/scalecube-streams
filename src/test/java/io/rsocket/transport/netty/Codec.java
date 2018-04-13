package io.rsocket.transport.netty;

import io.rsocket.Payload;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.api.GreetingResponse;
import io.rsocket.util.DefaultPayload;

public class Codec {

	
	public static Payload encode(GreetingResponse mapper) {
		return DefaultPayload.create(mapper.greeting());
	}

	public static GreetingRequest decode(Payload payload) {
		return new GreetingRequest(DefaultPayload.create(payload).getDataUtf8());
	}
}
