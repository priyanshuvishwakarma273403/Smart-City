package com.smartCity.TrafficService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TrafficServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficServiceApplication.class, args);
	}

}
