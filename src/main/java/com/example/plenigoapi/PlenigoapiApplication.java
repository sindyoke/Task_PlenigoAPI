package com.example.plenigoapi;

import com.example.plenigoapi.service.PlenigoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PlenigoapiApplication {

	@Autowired
	private PlenigoApiService plenigoApiService;

	public static void main(String[] args) {
		SpringApplication.run(PlenigoapiApplication.class, args);
	}

	@Bean
	CommandLineRunner run() {
		return args -> {
			int numberOfOrders = args.length > 0 ? Integer.parseInt(args[0]) : 300;
			plenigoApiService.processOrders(numberOfOrders);
		};
	}
}
