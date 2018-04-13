package io.rsocket.transport.netty;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

public class Codec {

	
	public static Payload encode(GreetingResponse mapper) {
		return DefaultPayload.create(mapper.greeting());
	}

	public static GreetingRequest decode(Payload payload) {
		return new GreetingRequest(DefaultPayload.create(payload).getDataUtf8());
	}
}
