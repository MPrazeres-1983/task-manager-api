package com.mprazeres.taskmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Manager API",
                version = "1.0.0",
                description = "Production-grade REST API for task management with JWT authentication, " +
                              "pagination, and filtering. Built with Java 17 + Spring Boot 3.",
                contact = @Contact(
                        name = "Mário Prazeres",
                        url = "https://github.com/MPrazeres-1983"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development"),
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Bearer token. Obtain via POST /api/auth/login",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
