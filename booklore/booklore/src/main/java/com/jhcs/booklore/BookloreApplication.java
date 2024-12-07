package com.jhcs.booklore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@EnableJpaAuditing
@Async
public class BookloreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookloreApplication.class, args);
	}

}
