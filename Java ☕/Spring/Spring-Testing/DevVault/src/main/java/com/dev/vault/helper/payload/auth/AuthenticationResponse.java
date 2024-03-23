package com.dev.vault.helper.payload.auth;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String username;
    private List<String> roles;
    private List<String> rolesDescription;
    private String token;

    // constructor, getters, and setters
}
