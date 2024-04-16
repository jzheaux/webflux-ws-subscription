package com.example.greeting;

import java.time.Duration;

import reactor.core.publisher.Flux;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	@QueryMapping
	String greeting() {
		return "Hello World!";
	}

	@SubscriptionMapping
	Flux<String> greetings() {
		return Flux.interval(Duration.ofMillis(50)).map(aLong -> "Hello " + aLong + "!");
	}

}
