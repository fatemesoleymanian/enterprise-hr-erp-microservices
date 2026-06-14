package com.example.identity_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@OpenAPIDefinition(info = @Info(
		title = "Identity Service REST APIs",
		description = "Identity Service REST APIs Documentation",
		version = "v1.0",
		contact = @Contact(
				name = "Fateme Soleymanian",
				email = "soleymanian.usc@gmail.com"
		)
))
@SpringBootApplication
@ConfigurationPropertiesScan
public class IdentityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityServiceApplication.class, args);
	}

}
