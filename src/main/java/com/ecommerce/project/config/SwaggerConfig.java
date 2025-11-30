package com.ecommerce.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customConfig() {
		SecurityScheme bearerScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
				.bearerFormat("JWT").description("Enter JWT token");

		SecurityRequirement bearerRequirement = new SecurityRequirement().addList("Bearer Authentication");
		return new OpenAPI().info(new Info().title("Springboot E-Commerce API") //  THIS CHANGES THE TITLE
				.version("v1.0").description("API documentation for your e-commerce system"))
				.components(new Components().addSecuritySchemes("Bearer Authentication", bearerScheme))
				.addSecurityItem(bearerRequirement);

	}

}
