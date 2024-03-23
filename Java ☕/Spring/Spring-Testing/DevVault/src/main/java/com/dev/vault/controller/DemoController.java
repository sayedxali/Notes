package com.dev.vault.controller;

import com.dev.vault.config.jwt.JwtService;
import com.dev.vault.helper.payload.auth.AuthenticationRequest;
import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dev.vault.model.user.enums.Role.TEAM_MEMBER;

@RestController
@RequiredArgsConstructor
public class DemoController {

    @GetMapping("/testing/controller/getAll")
    public List<String> getAll() {
        return List.of(
                "hello!",
                "SeyedAli",
                "today",
                "i",
                "got",
                "a",
                "call",
                "for",
                "interview"
        );
    }

    @PostMapping("/token")
    public Map<String, String> generateToken(@RequestBody AuthenticationRequest authenticationRequest) {
        Roles roles = Roles.builder().role(TEAM_MEMBER).build();
        User user = User.builder()
                .email(authenticationRequest.getEmail())
                .password(authenticationRequest.getPassword())
                .roles(Set.of(roles))
                .build();
        return Map.of("token", JwtService.generateToken(user));
    }

}
