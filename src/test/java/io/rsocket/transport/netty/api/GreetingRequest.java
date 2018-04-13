package io.rsocket.transport.netty.api;

public class GreetingRequest {

	@Override
	public String toString() {
		return "GreetingRequest [name=" + name + "]";
	}

	private String name;

	public GreetingRequest(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}
}
