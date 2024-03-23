package com.dev.vault.controller.authentication;

import com.dev.vault.helper.payload.auth.AuthenticationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Tag("integration")
@DisplayName("Integration tests for Authentication API endpoints")
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AuthenticationControllerIntegrationTest {

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private @Value("${api.endpoint.base-url}") String baseUrl;

    private String token;

    @BeforeEach
    public void setUp() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("seyed@gmail.com", "pass");

        String json = this.objectMapper.writeValueAsString(authenticationRequest);

        MvcResult mvcResult = this.mockMvc.perform(
                        post(this.baseUrl + "/auth")
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        this.token = new JSONObject(contentAsString).getString("token");
    }

    @Test
    void test() throws Exception {
        System.out.println("token -> " + this.token);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("seyed@gmail.com", "pass");

        String json = this.objectMapper.writeValueAsString(authenticationRequest);

        ResultActions response = this.mockMvc.perform(
                post(this.baseUrl + "/auth")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.username", is("seyed")))
        ;
    }

}
