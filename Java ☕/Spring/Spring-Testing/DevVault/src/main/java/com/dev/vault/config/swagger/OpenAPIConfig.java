package com.dev.vault.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "SeyedAli",
                        email = "Sayedxali7@gmail.com",
                        url = "http://github.com/sayedxali"
                ),
                description = "Iam a Java Developer! I primarily do Back-end using java, but recently I started learning Android with Java/Kotlin ;)",
                license = @License(
                        name = "My Licence",
                        url = "uhh...(*￣０￣)ノ"
                ),
                title = "OpenAPI Documentation - Swagger3 :: Spring Security - DevVault",
                version = "1.0",
                termsOfService = "You are free to use all my free APIs!"
        ),
        servers = {
                @Server(
                        description = "Local DEV_ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Local PROD_ENV",
                        url = "http://seyedali.duke.of.java"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT based Authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER // inject the jwt in the header
)
public class OpenAPIConfig {
}
