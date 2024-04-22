package com.example.greeting;

import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class WebfluxWsSubscriptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxWsSubscriptionApplication.class, args);
	}

	/*@Bean
	SecurityFilterChain filters(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll());
		return http.build();
	}*/

	@Bean
	SecurityWebFilterChain webFilters(ServerHttpSecurity http) {
		http.authorizeExchange((authorize) -> authorize.anyExchange().permitAll());
		return http.build();
	}

	@Bean
	public AuthenticationWebSocketGraphQlInterceptor authWebSocketGraphQlInterceptor(@Value("classpath:simple.pub") RSAPublicKey pub) {
		ReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withPublicKey(pub).build();
		return new AuthenticationWebSocketGraphQlInterceptor(new BearerTokenAuthenticationConnectionInitializationConverter(), new JwtReactiveAuthenticationManager(decoder));
	}

}
