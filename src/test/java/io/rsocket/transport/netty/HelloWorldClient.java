package io.rsocket.transport.netty;


import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import reactor.core.publisher.Mono;

public final class HelloWorldClient {

  private Timer latency;
  
  public static void main(String[] args) throws InterruptedException {
    
     
    final int count = 500_000;
    CountDownLatch latch = new CountDownLatch(count);

    AtomicInteger send = new AtomicInteger(0);

    RSocketFactory.receive()
        .acceptor(
            (setupPayload, reactiveSocket) -> Mono.just(
                new AbstractRSocket() {
                  @Override
                  public Mono<Payload> requestResponse(Payload p) {
                    return Mono.just(p);
                  }
                }))
        .transport(TcpServerTransport.create("localhost", 7000))
        .start()
        .subscribe();

    RSocket socket =
        RSocketFactory.connect()
            .transport(TcpClientTransport.create("localhost", 7000))
            .start()
            .block();
    
    warmup(count, socket);
    
    long start = System.currentTimeMillis();
    for (int i = 0; i < count; i++) {
      
      socket.requestResponse(DefaultPayload.create("Hello world"))
          .subscribe(consumer -> {
            latch.countDown();
          });
      send.incrementAndGet();
    }

    System.out.println(
        "done sending: " + latch.getCount() + "/" + send.get() + " time:" + (System.currentTimeMillis() - start));
    
    latch.await(160, TimeUnit.SECONDS);
    long time = (System.currentTimeMillis() - start);

    System.out.println("done reciving: " + latch.getCount() + "/" + send.get() + " time:" + time);

    System.out.println("total request/replay per sec: " + ((count / time) * 1000));
  }

  private static void warmup(final int count, RSocket socket) {
    for (int i = 0; i < count/10; i++) 
    socket.requestResponse(DefaultPayload.create("Hello world"))
    .subscribe(consumer -> {
    });
  }
}
