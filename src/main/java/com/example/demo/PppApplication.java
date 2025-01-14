package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class PppApplication {

	public static void main(String[] args) {
		log.info("Application is starting...");

		try {
			SpringApplication.run(PppApplication.class, args);
			log.info("Application has started successfully.");
		} catch (Exception e) {
			if (e.getClass().getName().contains("SilentExitException")) {
				log.debug("Spring is restarting the main thread - See spring-boot-devtools");
			} else {
				log.error("Application crashed!", e);
			}
		}
	}
}
