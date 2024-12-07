package com.okarath.assessment;

import org.springframework.boot.SpringApplication;

public class TestMatchOddsAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(MatchOddsAppApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
