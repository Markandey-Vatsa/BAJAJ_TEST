package com.example.demo;

import com.example.demo.service.WebhookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(WebhookService webhookService) {
		return args -> {
			System.out.println("=== Starting Webhook Workflow ===\n");
			webhookService.executeWorkflow();
			System.out.println("\n=== Workflow Execution Completed ===");
		};
	}

}
