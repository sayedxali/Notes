package com.example.springswagger2.controller;

import com.example.springswagger2.model.entity.User;
import com.example.springswagger2.model.request.AuthenticationRequest;
import com.example.springswagger2.model.request.RegisterRequest;
import com.example.springswagger2.model.response.AuthenticationResponse;
import com.example.springswagger2.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * The AuthenticationController class is REST controller that handles the authentication and authorization of users.
 * It contains methods for registering a new user, verifying a user's account, and authenticating a user.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")

@Tag(name = "Authentication API")
@SecurityRequirement(name = "bearerAuth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    /**
     * The register method registers a new user with the system.
     * It takes a RegisterRequest object as input and returns an AuthenticationResponse object.
     * The method is annotated with @PostMapping and @Valid to handle HTTP POST requests and validate the input.
     *
     * @param registerRequest The RegisterRequest object containing the user's information.
     * @return The AuthenticationResponse object containing the user's authentication token.
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register",
            description = "Get endpoint for registering user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid token",
                            responseCode = "403"
                    )
            },
            parameters = {
                    @Parameter(
                            name = "RegisterRequest",
                            description = "The request object with username and password field",
                            in = ParameterIn.DEFAULT,
                            allowEmptyValue = false,
                            example = "username: admin | password: admin",
                            required = true
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return new ResponseEntity<>(authenticationService.registerUser(registerRequest), HttpStatus.CREATED);
    }


    /**
     * The authenticate method authenticates a user with the system.
     * It takes an AuthenticationRequest object as input and returns an AuthenticationResponse object.
     * The method is annotated with @PostMapping to handle HTTP POST requests.
     *
     * @param authenticationRequest The AuthenticationRequest object containing the user's credentials.
     * @return The AuthenticationResponse object containing the user's authentication token.
     */
    @PostMapping({"/login", "/authenticate"})
    @Operation(summary = "Login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }


    @GetMapping("/current-user")
    @Operation(summary = "Get Current Logged-in User")
    @Hidden
    public User currentUser() {
        return authenticationService.getCurrentUser();
    }

}
