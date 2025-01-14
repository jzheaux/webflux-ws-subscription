package com.example.greeting;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.WebSocketGraphQlTester;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebfluxWsSubscriptionApplicationTests {

	@LocalServerPort
	private int port;

	@Value("http://localhost:${local.server.port}${spring.graphql.websocket.path}")
	private String baseUrl;

	private GraphQlTester graphQlTester;


	@BeforeEach
	void setUp() {
		URI url = URI.create(baseUrl);
		this.graphQlTester = WebSocketGraphQlTester.builder(url, new ReactorNettyWebSocketClient()).build();
	}

	@Test
	void greeting() {
		this.graphQlTester.document("{greeting}")
				.execute()
				.path("greeting")
				.entity(String.class).isEqualTo("Hello World!");
	}

	@Test
	void greetings() {
		Flux<String> flux = this.graphQlTester.document("subscription {greetings}")
				.executeSubscription()
				.toFlux("greetings.", String.class);

		StepVerifier.create(flux)
				.expectNext("Hello 0!", "Hello 1!", "Hello 2!", "Hello 3!")
				.thenCancel()
				.verify();
	}

}
