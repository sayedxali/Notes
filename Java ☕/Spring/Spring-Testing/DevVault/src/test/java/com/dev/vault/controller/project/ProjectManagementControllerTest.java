package com.dev.vault.controller.project;

import com.dev.vault.config.jwt.filter.JwtAuthenticationFilter;
import com.dev.vault.helper.payload.group.ProjectDto;
import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.project.ProjectMembersRepository;
import com.dev.vault.repository.project.ProjectRepository;
import com.dev.vault.repository.project.UserProjectRoleRepository;
import com.dev.vault.repository.user.UserRepository;
import com.dev.vault.service.interfaces.project.ProjectManagementService;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import com.dev.vault.util.repository.RepositoryUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.dev.vault.model.user.enums.Role.TEAM_MEMBER;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectManagementController.class)
class ProjectManagementControllerTest {

    private @MockBean ProjectManagementService projectManagementService;

    //<editor-fold desc="beans required in `ProjectManagementServiceImpl` class">
    private @MockBean UserRepository userRepository;
    private @MockBean UserProjectRoleRepository userProjectRoleRepository;
    private @MockBean ProjectMembersRepository projectMembersRepository;
    private @MockBean ProjectRepository projectRepository;
    private @MockBean ModelMapper modelMapper;
    private @MockBean AuthenticationService authenticationService;
    private @MockBean RepositoryUtils repositoryUtils;
    private @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    //</editor-fold>

    //<editor-fold desc="dependencies for testing">
    private @Autowired MockMvc mockMvc; // to call REST APIs
    private @Autowired ObjectMapper objectMapper;
    //</editor-fold>
    private ProjectDto mockProjectDTO;

    @Test
    @DisplayName("➕ Create Project Operation")
    public void givenProjectDTOObj_WhenCreateProject_ThenReturnCreatedProjectDTOObj() throws Exception {
        // given (precondition or setup)
        given(projectManagementService.createProject(mockProjectDTO))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when (action occurs / action or the behaviour that we are going to test)
        ResultActions response = mockMvc.perform(
                post("/api/v1/proj_leader/create-project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProjectDTO))
                        .with(user("mockAuthenticatedUser").roles("TEAM_MEMBER", "PROJECT_LEADER").password("mockPass0!"))
                        .with(csrf())
                        .header(AUTHORIZATION, "Bearer token123!")
        );

        // then (verify the output)
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectName", is(mockProjectDTO.getProjectName())))
                .andExpect(jsonPath("$.projectDescription", is(mockProjectDTO.getProjectDescription())))
                .andExpect(jsonPath("$.createdAt", is(LocalDate.now())))
                .andExpect(jsonPath("$.creationTime", is(LocalTime.now())));
    }

    @Test
    public void demoGet() throws Exception {
        Roles teamMemberRole = Roles.builder().role(TEAM_MEMBER).build();
//        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MzY3NTY2QjU5NzAzMzczMzY3NjM4NzkyRjQyM0Y0NTI4NDgyQjRENjI1MTY1NTQ2ODU3NkQ1QTcxMzQ3NDM3IiwibmFtZSI6InVzZXIiLCJpYXQiOjE1MTYyMzkwMjJ9.nA_vdNz7ISKdGmAyJC0_BxS96CgVoJ13lEiwkRMKQUU";
        String token = generateToken();

        ResultActions response = mockMvc.perform(
                get("/api/v1/search_project")
                        .with(user("user").password("pass").roles(String.valueOf(TEAM_MEMBER)))
                        .header(AUTHORIZATION, "Bearer " + token)
//                        .accept(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.projectName", is("hello!")));
    }

    public String generateToken() {
        UserDetails userDetails = User.builder().username("user").password("pass").roles(new HashSet<>()).build();
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode("5367566B59703373367638792F423F4528482B4D6251655468576D5A71347437");
        return Keys.hmacShaKeyFor(keyBytes);
    }


}