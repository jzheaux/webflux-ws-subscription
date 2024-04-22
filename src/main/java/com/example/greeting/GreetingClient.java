package com.example.greeting;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import reactor.core.publisher.Mono;

import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.client.GraphQlClientInterceptor;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClientInterceptor;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

public class GreetingClient {

	public static void main(String[] args) throws IOException {
		String token = mintToken();
		GraphQlClientInterceptor interceptor = new WebSocketGraphQlClientInterceptor() {
					@Override
					public Mono<Object> connectionInitPayload() {
						return Mono.just(Map.of("Authorization", "Bearer " + token));
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

	private static String mintToken() throws IOException {
		try (InputStream priv = new ClassPathResource("simple.priv").getInputStream();
			 InputStream pub = new ClassPathResource("simple.pub").getInputStream()) {
			RSAKey key = new RSAKey.Builder(RsaKeyConverters.x509().convert(pub))
				.privateKey(RsaKeyConverters.pkcs8().convert(priv)).build();
			JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
			return encoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder()
				.subject("user").claim("scope", "greeting:read").build())).getTokenValue();
		}
	}
}
