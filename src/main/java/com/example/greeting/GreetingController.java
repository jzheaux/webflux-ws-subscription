package com.example.greeting;

import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Flux;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	private static final Log logger = LogFactory.getLog(GreetingController.class);


	@QueryMapping
	String greeting() {
		return "Hello World!";
	}

	@SubscriptionMapping
	Flux<String> greetings() {
		return Flux.interval(Duration.ofMillis(50))
				.contextWrite(context -> {
					logger.debug("Token '" + context.get(AuthWebSocketGraphQlInterceptor.TOKEN_NAME) + "'");
					return context;
				})
				.map(aLong -> "Hello " + aLong + "!");
	}

}
