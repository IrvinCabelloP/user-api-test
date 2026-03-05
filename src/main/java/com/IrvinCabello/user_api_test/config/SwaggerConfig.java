package com.IrvinCabello.user_api_test.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User API")
                        .version("1.0.0")
                        .description("REST API para gestión de usuarios — Prueba Técnica Chakray Consulting")
                        .contact(new Contact()
                                .name("Irvin Cabello")
                                .email("irvin@mail.com")));
    }
}