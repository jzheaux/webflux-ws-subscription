package com.example.greeting;

import java.net.URI;
import java.util.Map;

import reactor.core.publisher.Mono;

import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClientInterceptor;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

public class GreetingClient {

	public static void main(String[] args) {

		WebSocketGraphQlClientInterceptor interceptor = new WebSocketGraphQlClientInterceptor() {
					@Override
					public Mono<Object> connectionInitPayload() {
						return Mono.just(Map.of("token", "abc"));
					}
				};

		WebSocketGraphQlClient graphQlCclient = WebSocketGraphQlClient
				.builder(URI.create("ws://localhost:8080/graphql"), new ReactorNettyWebSocketClient())
				.interceptor(interceptor)
				.build();

		graphQlCclient.document("subscription {greetings}")
				.retrieveSubscription("greetings")
				.toEntity(String.class)
				.take(5)
				.doOnNext(System.out::println)
				.blockLast();
	}

}
