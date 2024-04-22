package com.example.greeting;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;

import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketGraphQlRequest;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

public class AuthenticationWebSocketGraphQlInterceptor implements WebSocketGraphQlInterceptor {

	private static final Log logger = LogFactory.getLog(AuthenticationWebSocketGraphQlInterceptor.class);

	private static final String AUTHENTICATION_KEY_NAME = AuthenticationWebSocketGraphQlInterceptor.class.getName() + ".authentication";

	private final AuthenticationConnectionInitializationConverter authenticationConverter;
	private final ReactiveAuthenticationManager authenticationManager;

	public AuthenticationWebSocketGraphQlInterceptor(AuthenticationConnectionInitializationConverter authenticationConverter,
			ReactiveAuthenticationManager authenticationManager) {
		this.authenticationConverter = authenticationConverter;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Mono<Object> handleConnectionInitialization(WebSocketSessionInfo sessionInfo,
				Map<String, Object> connectionInitPayload) {
		return this.authenticationConverter.convert(connectionInitPayload)
			.doOnNext((authentication) -> logger.debug("Found " + authentication + " to authenticate"))
			.doOnNext((authentication) -> sessionInfo.getAttributes().put(AUTHENTICATION_KEY_NAME, authentication))
			.then(Mono.empty());
	}

	@Override
	public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
		if (!(request instanceof WebSocketGraphQlRequest wsRequest)) {
			return chain.next(request);
		}
		WebSocketSessionInfo info = wsRequest.getSessionInfo();
		Authentication authentication = (Authentication) info.getAttributes().get(AUTHENTICATION_KEY_NAME);
		Mono<SecurityContext> context = this.authenticationManager.authenticate(authentication)
			.doOnNext((result) -> logger.debug("Authenticated " + result)).map(SecurityContextImpl::new);
		return chain.next(request).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(context));
	}

}
