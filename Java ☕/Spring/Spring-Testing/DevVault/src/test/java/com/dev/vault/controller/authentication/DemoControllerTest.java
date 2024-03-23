package com.dev.vault.controller.authentication;

import com.dev.vault.config.jwt.JwtService;
import com.dev.vault.controller.DemoController;
import com.dev.vault.helper.payload.auth.AuthenticationRequest;
import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import com.dev.vault.model.user.enums.Role;
import com.dev.vault.repository.user.UserRepository;
import com.dev.vault.repository.user.VerificationTokenRepository;
import com.dev.vault.repository.user.jwt.JwtTokenRepository;
//import com.dev.vault.service.module.mail.MailService;
import com.dev.vault.util.repository.RepositoryUtils;
import com.dev.vault.util.user.AuthenticationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DemoController.class)
public class DemoControllerTest {

    private @Autowired MockMvc mockMvc; // to call REST APIs

    //<editor-fold desc="dependencies of the service and controller class">
    private @MockBean VerificationTokenRepository verificationTokenRepository;
    private @MockBean UserRepository userRepository;
    private @MockBean JwtService jwtService;
    private @MockBean PasswordEncoder passwordEncoder;
//    private @MockBean MailService mailService;
    private @MockBean ModelMapper modelMapper;
    private @MockBean AuthenticationManager authenticationManager;
    private @MockBean UserDetailsService userDetailsService;
    private @MockBean RepositoryUtils repositoryUtils;
    private @MockBean AuthenticationUtils authenticationUtils;
    private @MockBean JwtTokenRepository jwtTokenRepository;
    //</editor-fold>

    private User user;
    private Roles roles;

    @BeforeEach
    public void setup() {
        roles = Roles.builder().role(Role.TEAM_MEMBER).build();
        user = User.builder()
                .email("test@gmail.com")
                .password("pass")
                .roles(Set.of(roles))
                .build();
    }

    @Test
    public void test_listAll() throws Exception {
        String token = JwtService.generateToken(user);
        System.out.println(token);
        mockMvc.perform(
                        get("/testing/controller/getAll")
                                .with(user("user").password("pass").roles("TEAM_MEMBER"))
                                .header(AUTHORIZATION, "Bearer " + token)
                ).andDo(print())
                .andExpect(jsonPath("$[0]", is("hello!")));
    }


    @Test
    @DisplayName("➕ Generate JWT Token Operation")
    public void givenUserDetails_WhenAuthenticate_ThenReturnUserDetailsAndJWTToken() throws Exception {
        // given (precondition or setup)
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder().email(user.getEmail()).password(user.getPassword()).build();

        String token = JwtService.generateToken(user);
        System.out.println(token);

        // when (action occurs / action or the behaviour that we are going to test)
        ResultActions response = this.mockMvc.perform(
                post("/token")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(authenticationRequest))
                        .with(csrf())
//                        .with(user("test@gmail.com").password("pass"))
                        .header(AUTHORIZATION, "Bearer " + token)
        );

        // then (verify the output)
        response.andDo(print())
                .andExpect(jsonPath("$.token", is(token)));
    }


}
