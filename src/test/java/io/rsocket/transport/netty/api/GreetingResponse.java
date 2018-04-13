package io.rsocket.transport.netty.api;

public class GreetingResponse {
  private CharSequence greeting;

  public GreetingResponse(String greeting) {
    this.greeting = greeting;
  }

  public CharSequence greeting() {
    return greeting;
  }

  @Override
  public String toString() {
    return "GreetingResponse [greeting=" + greeting + "]";
  }



}
