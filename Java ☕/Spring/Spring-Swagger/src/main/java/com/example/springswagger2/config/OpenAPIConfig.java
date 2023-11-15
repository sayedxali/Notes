package com.example.springswagger2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "SeyedAli",
                        email = "Sayedxali7@gmail.com",
                        url = "https://github.com/sayedxali"
                ),
                description = "Iam a Java Developer. An aspiring one at that!",
                license = @License(
                        name = "My Licence",
                        url = "uhhhh..."
                ),
                title = "OpenAPI Documentation - Swagger3 :: Spring Security",
                version = "1.0",
                termsOfService = "You are free to use all my free APIs!"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Local PROD",
                        url = "http://seyedali.humble.learner"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER // inject the jwt in header
)
@Configuration
public class OpenAPIConfig {


}
