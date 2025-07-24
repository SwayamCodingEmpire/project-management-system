package com.cozentus.pms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class PmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PmsApplication.class, args);
	}

}
