package io.rsocket.transport.netty.api;

public class GreetingResponse {

	@Override
	public String toString() {
		return "GreetingResponse [greeting=" + greeting + "]";
	}

	private CharSequence greeting;

	public CharSequence greeting() {
		return greeting;
	}

}
