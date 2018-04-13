package io.rsocket.transport.netty;

public class GreetingRequest {

	private String name;

	public GreetingRequest(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}
}
