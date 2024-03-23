package com.dev.vault.controller.authentication;

import com.dev.vault.config.jwt.JwtService;
import com.dev.vault.controller.DemoController;
import com.dev.vault.helper.payload.auth.AuthenticationRequest;
import com.dev.vault.helper.payload.auth.AuthenticationResponse;
import com.dev.vault.helper.payload.auth.RegisterRequest;
import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.user.UserRepository;
import com.dev.vault.repository.user.VerificationTokenRepository;
import com.dev.vault.repository.user.jwt.JwtTokenRepository;
import com.dev.vault.service.interfaces.user.AuthenticationService;
//import com.dev.vault.service.module.mail.MailService;
import com.dev.vault.util.repository.RepositoryUtils;
import com.dev.vault.util.user.AuthenticationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;

import static com.dev.vault.model.user.enums.Role.TEAM_MEMBER;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {AuthenticationController.class, DemoController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
//@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    private @MockBean AuthenticationService authenticationService;

    private @Autowired MockMvc mockMvc; // to call REST APIs
    private @Autowired ObjectMapper objectMapper;

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

    //<editor-fold desc="objects to be created before each method">
    private RegisterRequest mockRegisterRequest;
    private AuthenticationResponse mockAuthenticationResponse;
    private User mockFoundUser, mockMapedUser;
    private Roles mockTeamMemberRole;
    //</editor-fold>

    @BeforeEach
    public void setup() {
        mockRegisterRequest = RegisterRequest.builder().username("MockUsername").password("MockPass0!").email("MockEmail@gmail.com").build();
        mockFoundUser = User.builder().userId(1L).username("MockFoundUser").password("MockFoundUserPass0!").email("MockFoundUserEmail@gmail.com").build();
        mockMapedUser = User.builder().userId(1L).username("MockFoundUser").password("MockFoundUserPass0!").email("MockFoundUserEmail@gmail.com").build();
        mockTeamMemberRole = Roles.builder().roleId(1L).role(TEAM_MEMBER).users(Set.of(mockMapedUser, mockFoundUser)).build();
        mockAuthenticationResponse = AuthenticationResponse.builder().username(mockMapedUser.getUsername()).token("MOCK_TOKEN").build();
    }

    @Test
    @DisplayName("➕ Register New User Operation")
    public void givenProjectDTOObj_WhenCreateProject_ThenReturnCreatedProjectDTOObj() throws Exception {
        // given (precondition or setup)
//        mockMapedUser.setRoles(Set.of(mockTeamMemberRole));
//        mockMapedUser.setPassword(passwordEncoder.encode(mockMapedUser.getPassword()));
/*


        when(userRepository.findByEmail(mockRegisterRequest.getEmail()))
                .thenReturn(Optional.of(mockFoundUser)); // negative scenario!
        when(repositoryUtils.findRoleByRole_OrElseThrow_ResourceNotFoundException(TEAM_MEMBER))
                .thenReturn(mockTeamMemberRole); // negative scenario!
        when(modelMapper.map(mockRegisterRequest, User.class))
                .thenReturn(mockMapedUser);
        when(userRepository.save(mockMapedUser))
                .thenReturn(mockMapedUser);
        when(authenticationUtils.generateVerificationToken(mockMapedUser))
                .thenReturn("MOCKED_GENERATED_VERIFICATION_TOKEN");
        doNothing().when(mailService).sendEmail(any(Email.class));
        when(jwtService.generateToken(mockMapedUser))
                .thenReturn("MOCKED_JWT_TOKEN");
        doNothing().when(authenticationUtils).buildAndSaveJwtToken(any(User.class), anyString());
*/
        RegisterRequest registerRequest = RegisterRequest.builder().username("MockUsername").build();
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().username(registerRequest.getUsername()).roles(List.of("Mock_TEAM_MEMBER")).token("MOCK_TOKEN").build();

        String json = objectMapper.writeValueAsString(mockRegisterRequest);

        when(authenticationService.registerUser(any(RegisterRequest.class)))
                .thenReturn(authenticationResponse);

        // when (action occurs / action or the behaviour that we are going to test)

        String baseUrl = "/api/v1";
        ResultActions response = mockMvc.perform(
                post(baseUrl + "/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(json)
//                        .with(csrf()) // since we didn't disable csrf in the test or else we'll get `forbidden`
        );

        // then (verify the output)
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(registerRequest.getUsername())))
                .andExpect(jsonPath("$.roles[0]", is("Mock_TEAM_MEMBER")))
                .andExpect(jsonPath("$.token", is("MOCK_TOKEN")))
        ;
    }

    @Test
    @DisplayName("Login Success Operation")
    public void givenAuthenticationRequest_WhenAuthenticate_ThenReturnAuthenticationResponseObj() throws Exception {
        // given (precondition or setup)
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("seyed@gmail.com", "pass");

        String json = this.objectMapper.writeValueAsString(authenticationRequest);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setUsername(authenticationRequest.getEmail());
        authenticationResponse.setRoles(List.of("TEAM_MEMBER", "PROJECT_ADMIN"));

        when(this.authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authenticationResponse);

        // when (action occurs / action or the behaviour that we are going to test)
        ResultActions response = this.mockMvc.perform(
                post("/api/v1/auth")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        // then (verify the output)
        response.andDo(print())
                .andExpect(jsonPath("$.token").isNotEmpty())
        ;
    }

}