package com.example.greeting;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reactor.core.publisher.Mono;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.util.StringUtils;

public final class BearerTokenAuthenticationConnectionInitializationConverter implements AuthenticationConnectionInitializationConverter {
	private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
		Pattern.CASE_INSENSITIVE);

	@Override
	public Mono<Authentication> convert(Map<String, Object> connectionInitPayload) {
		return resolveFromAuthorizationKey(connectionInitPayload).map(BearerTokenAuthenticationToken::new);
	}

	private Mono<String> resolveFromAuthorizationKey(Map<String, Object> connectionInitPayload) {
		String authorization = (String) connectionInitPayload.get("Authorization");
		if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
			return Mono.empty();
		}
		Matcher matcher = authorizationPattern.matcher(authorization);
		if (!matcher.matches()) {
			BearerTokenError error = invalidTokenError();
			return Mono.error(new OAuth2AuthenticationException(error));
		}
		return Mono.just(matcher.group("token"));
	}

	private static BearerTokenError invalidTokenError() {
		return BearerTokenErrors.invalidToken("Bearer token is malformed");
	}
}
