package io.rsocket.transport.netty;

import io.rsocket.Payload;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.api.GreetingResponse;
import io.rsocket.util.DefaultPayload;

public class Codec {

  public static Payload toPayload(GreetingRequest request) {
    return DefaultPayload.create(request.name());
  }

  public static GreetingRequest toRequest(Payload payload) {
    return new GreetingRequest(DefaultPayload.create(payload).getDataUtf8());
  }


  public static Payload toPayload(GreetingResponse response) {
    return DefaultPayload.create(response.greeting());
  }

  public static GreetingResponse toResponse(Payload payload) {
    return new GreetingResponse(DefaultPayload.create(payload).getDataUtf8());
  }
}
