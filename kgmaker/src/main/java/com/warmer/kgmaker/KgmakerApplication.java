package com.warmer.kgmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class KgmakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KgmakerApplication.class, args);
	}
}
