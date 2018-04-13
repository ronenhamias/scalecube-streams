package io.rsocket.transport.netty;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.api.GreetingRequest;
import io.rsocket.transport.netty.server.TcpServerTransport;

import org.reactivestreams.Publisher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class DuplexGreetingExample {

  public static void main(String[] args) throws InterruptedException {
    MetricRegistry registry = new MetricRegistry();

    ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();

    reporter.start(5, TimeUnit.SECONDS);
    Metrics metrics = new Metrics(registry);
    int count = 600_000;

    GreetingServiceImpl service = new GreetingServiceImpl();

    // provision a service on port 7000.
    RSocketFactory.receive().acceptor((setup, reactiveSocket) -> Mono.just(new AbstractRSocket() {
      @Override
      public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return service.sayHellos(Flux.from(payloads).map(Codec::toRequest)).map(Codec::toPayload);
      }
    })).transport(TcpServerTransport.create("localhost", 7000)).start().subscribe();

    // interact with the service on port 7000.
    GreetingServiceProxy proxy = new GreetingServiceProxy();

    long startTime = System.currentTimeMillis();

    CountDownLatch countLatch = new CountDownLatch(count);

    Flux<GreetingRequest> requests = Flux.from(subscriber -> {
      for (int i = 0; i < count; i++) {
        subscriber.onNext(new GreetingRequest("ronen"));
      }
    });

    proxy.sayHellos(requests).subscribe(response -> {
      // System.out.println(response);
      countLatch.countDown();
      metrics.getCounter("sayHello", "response").inc(1);
    });

    System.out.println("Finished sending " + count + " messages in " + (System.currentTimeMillis() - startTime));
    countLatch.await(60, TimeUnit.SECONDS);

    System.out.println("Finished receiving " + (count - countLatch.getCount()) + " messages in "
        + (System.currentTimeMillis() - startTime));

    System.out.println("Rate: " + ((count - countLatch.getCount()) / ((System.currentTimeMillis() - startTime) / 1000))
        + " round-trips/sec");
  }
}
