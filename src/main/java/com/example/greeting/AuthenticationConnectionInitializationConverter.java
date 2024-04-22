package com.example.greeting;

import java.util.Map;

import reactor.core.publisher.Mono;

import org.springframework.security.core.Authentication;

public interface AuthenticationConnectionInitializationConverter {
	Mono<Authentication> convert(Map<String, Object> connectionInitPayload);
}
