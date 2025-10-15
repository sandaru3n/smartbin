package com.sliit.smartbin.smartbin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartbinApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartbinApplication.class, args);
	}

}
