package com.example.greeting;

import java.util.Map;

import reactor.core.publisher.Mono;

import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketGraphQlRequest;
import org.springframework.graphql.server.WebSocketSessionInfo;

public class AuthWebSocketGraphQlInterceptor implements WebSocketGraphQlInterceptor {

	private static final String TOKEN_NAME = "token";

	@Override
	public Mono<Object> handleConnectionInitialization(
			WebSocketSessionInfo sessionInfo, Map<String, Object> connectionInitPayload) {

		Object token = connectionInitPayload.get(TOKEN_NAME);
		sessionInfo.getAttributes().put("token", token);
		return Mono.empty();
	}

	@Override
	public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
		return chain.next(request).contextWrite(context -> {
			if (request instanceof WebSocketGraphQlRequest wsRequest) {
				WebSocketSessionInfo info = wsRequest.getSessionInfo();
				Object token = info.getAttributes().get(TOKEN_NAME);
				if (token != null) {
					return context.put(TOKEN_NAME, token);
				}
			}
			return context;
		});
	}

}
