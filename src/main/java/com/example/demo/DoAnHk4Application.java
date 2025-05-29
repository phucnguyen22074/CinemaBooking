package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoAnHk4Application {

	public static void main(String[] args) {
		SpringApplication.run(DoAnHk4Application.class, args);
	}

}
