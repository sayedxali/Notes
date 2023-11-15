package com.example.springswagger2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class SecuredAPI {

    @GetMapping
    @Operation(summary = "Secured API")
    public String securedURI() {
        return "Congratulations! You were able to access the `Secured API`";
    }

}
