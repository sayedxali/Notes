package com.example.springswagger2.service;

import com.example.springswagger2.config.jwt.JWTService;
import com.example.springswagger2.model.entity.User;
import com.example.springswagger2.model.request.AuthenticationRequest;
import com.example.springswagger2.model.request.RegisterRequest;
import com.example.springswagger2.model.response.AuthenticationResponse;
import com.example.springswagger2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse registerUser(RegisterRequest registerRequest) {
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .message("Successfully registered")
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String username = request.getUsername();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(username));

        return AuthenticationResponse.builder()
                .accessToken(token)
                .message("Hello '" + username + "'!")
                .build();
    }


    public User getCurrentUser() {
        // get the email of the currently authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseGet(User::new);
    }

}
