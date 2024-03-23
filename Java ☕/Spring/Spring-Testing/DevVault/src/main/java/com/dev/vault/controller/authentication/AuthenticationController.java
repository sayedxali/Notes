package com.dev.vault.controller.authentication;

import com.dev.vault.helper.exception.ResourceAlreadyExistsException;
import com.dev.vault.helper.payload.auth.AuthenticationRequest;
import com.dev.vault.helper.payload.auth.AuthenticationResponse;
import com.dev.vault.helper.payload.auth.RegisterRequest;
import com.dev.vault.helper.payload.dto.ApiResponse;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The AuthenticationController class is REST controller that handles the authentication and authorization of users.
 * It contains methods for registering a new user, verifying a user's account, and authenticating a user.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Authentication Controller API",
        description = "Controller class for: Registration, Authentication & Verification of email."
)
public class AuthenticationController {
    private AuthenticationService authenticationService;

    /**
     * The register method registers a new user with the system.
     * It takes a RegisterRequest object as input and returns an AuthenticationResponse object.
     * The method is annotated with @PostMapping and @Valid to handle HTTP POST requests and validate the input.
     *
     * @param registerRequest The RegisterRequest object containing the user's information.
     * @return The AuthenticationResponse object containing the user's authentication token.
     */
    @Operation(
            summary = "Register a new user",
            description = "The register method registers a new user with the system. It takes a RegisterRequest object as input and returns an AuthenticationResponse object.",
            parameters = {
                    @Parameter(
                            name = "RegisterRequest",
                            description = "The registration object with user's information fields such as email and password."
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful registration",
                                    value = "{\"username\": \"johndoe\", \"roles\": [\"admin\"], \"rolesDescription\": [\"Administrator\"], \"token\": \"abc123\"}",
                                    description = "The registration is successful."
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request : user already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceAlreadyExistsException.class),
                            examples = @ExampleObject(
                                    name = "User already exists",
                                    value = "{\"message\": \"User already exists\"}",
                                    description = "The user with the provided username is already present in the database."
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request : email and/or password format violated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceAlreadyExistsException.class),
                            examples = @ExampleObject(
                                    name = "Invalid email and/or password format",
                                    value = "{\"password\": \"Pass must be more than 8 characters & must contain a-z, A-Z, 0-9 and special characters.\", \"email\": \"must be a well-formed email address\"}",
                                    description = "Password and email must be in their respected format."
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request : email, password, username must not be null/blank",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceAlreadyExistsException.class),
                            examples = @ExampleObject(
                                    name = "Null/Blank email and/or password and/or username",
                                    value = "{\"password\": \"must not be null\", \"email\": \"must be a well-formed email address\", \"username\": \"must not be null\"}",
                                    description = "Password and email and username must not be Null/Blank."
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return new ResponseEntity<>(authenticationService.registerUser(registerRequest), HttpStatus.CREATED);
    }

    /**
     * The verifyAccount method verifies a user's account using a token.
     * It takes a token as input and returns an ApiResponse object.
     * The method is annotated with @RequestMapping to handle HTTP GET and POST requests.
     *
     * @param token The token used to verify the user's account.
     * @return The ApiResponse object containing the result of the verification.
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/accountVerification/{token}")
    public ResponseEntity<ApiResponse> verifyAccount(@PathVariable String token) {
        authenticationService.verifyAccount(token);
        return new ResponseEntity<>(new ApiResponse("account activated successfully", true), HttpStatus.OK);
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
    @Operation(
            summary = "Attempt login.",
            description = "The authenticate method authenticates a user with the system. It takes an AuthenticationRequest object as input and returns an AuthenticationResponse object.",
            parameters = {
                    @Parameter(
                            name = "AuthenticationRequest",
                            description = "A class with email & password field for authentication."
                    )
            }
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User authentication success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful registration",
                                    value = "{\"username\": \"johndoe\", \"roles\": [\"admin\"], \"rolesDescription\": [\"Administrator\"], \"token\": \"abc123\"}",
                                    description = "The registration is successful."
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User with the given email was not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceAlreadyExistsException.class),
                            examples = @ExampleObject(
                                    name = "User not found",
                                    value = "{\"message\": \"User with the given email was not found\"}",
                                    description = "The user with the provided email is not present in the database."
                            )
                    )
            )
    })
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}
