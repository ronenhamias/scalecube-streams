package io.rsocket.transport.netty;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import io.rsocket.transport.netty.api.GreetingRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import reactor.core.publisher.Flux;

public final class DuplexGreetingExample {

  public static void main(String[] args) throws InterruptedException {
    MetricRegistry registry = new MetricRegistry();

    ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();

    reporter.start(3000, TimeUnit.MILLISECONDS);
    Metrics metrics = new Metrics(registry);
    int count = 600_000;

    // provision server implementation
    // noinspection unused
    GreetingServiceImpl service = new GreetingServiceImpl();

    // interact with the service on port 7000.
    GreetingServiceProxy proxy = new GreetingServiceProxy();

    long startTime = System.currentTimeMillis();

    CountDownLatch countLatch = new CountDownLatch(count);

    Flux<GreetingRequest> requests = Flux.from(subscriber -> {
      for (int i = 0; i < count; i++) {
        subscriber.onNext(new GreetingRequest("ronen"));
        metrics.getCounter("sayHello", "request").inc(1);
      }
    });

    proxy.sayHellos(requests).subscribe(response -> {
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
