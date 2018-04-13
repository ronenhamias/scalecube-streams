package io.scalecube.streams.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.NettyContextCloseable;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.ByteBufPayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This starts TCP Server listening to 'reqStream' requests, and sends ticks in stream once receives request. Client
 * Sends request and reacts to received onNext event of server stream.
 */
public class RSocketClientServerRunner {

  public static final int PORT = 7200;
  private static final Duration TIMEOUT = Duration.ofSeconds(10);
  private static Logger log = LoggerFactory.getLogger(RSocketClientServerRunner.class);

  public static void main(String[] args) throws InterruptedException {
    log.info("Works");

    // Start server
    Mono<InetSocketAddress> serverAddr = startServer();
    Disposable subscribe = serverAddr.subscribe(addr -> {
      // Start Client
      Mono<RSocket> client = startClient(addr);
      client.subscribe(cl -> {
        // Send request
        makeRequestAndSyncWaitResponses(cl);
      });
    });

    Thread.currentThread().join();
  }

  private static void makeRequestAndSyncWaitResponses(RSocket client) {
    Payload request = ByteBufPayload.create("Hello");
    Flux<Payload> responseStream = client.requestStream(request);
    responseStream
        .map(Payload::getDataUtf8)
        .subscribe(n -> log.info("Client RCVD: {}", n));
    responseStream.blockLast(TIMEOUT);
    log.info("Closing Client...");
    client.dispose();
  }

  private static Mono<InetSocketAddress> startServer() {
    return RSocketFactory
        .receive()
        .acceptor(new ReqStreamTickingSocketAcceptor())
        .transport(TcpServerTransport.create(PORT)).start()
        .map(NettyContextCloseable::address);
  }

  private static Mono<RSocket> startClient(InetSocketAddress serverAddr) {
    return RSocketFactory
        .connect()
        .transport(TcpClientTransport.create(serverAddr))
        .start();
  }

}
