package com.fourspoons.mikkureomi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MikkureomiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MikkureomiApplication.class, args);
	}

}
