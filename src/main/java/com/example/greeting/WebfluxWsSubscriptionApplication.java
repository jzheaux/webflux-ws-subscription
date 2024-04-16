package com.example.greeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebfluxWsSubscriptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxWsSubscriptionApplication.class, args);
	}

	@Bean
	public AuthWebSocketGraphQlInterceptor authWebSocketGraphQlInterceptor() {
		return new AuthWebSocketGraphQlInterceptor();
	}

}
