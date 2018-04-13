package io.rsocket.transport.netty;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import io.rsocket.transport.netty.api.GreetingRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class RequestStreamGreetingExample {

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
    proxy.helloStream(new GreetingRequest("ronen"))
         .blockLast();

    System.out.println("Finished sending " + count + " messages in " + (System.currentTimeMillis() - startTime));

    System.out.println("Finished receiving " + (600_001) + " messages in "
        + (System.currentTimeMillis() - startTime));

    System.out.println("Rate: " + ((600_001) / ((System.currentTimeMillis() - startTime) / 1000))
        + " round-trips/sec");
  }
}