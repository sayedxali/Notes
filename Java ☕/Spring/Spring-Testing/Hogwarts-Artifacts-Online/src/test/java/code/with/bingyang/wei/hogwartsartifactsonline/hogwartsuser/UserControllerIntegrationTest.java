package code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Tag("integration")
@DisplayName("Integration tests for User API endpoints")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private @Value("${api.endpoint.base-url}") String baseUrl;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.post(this.baseUrl + "/users/login")
                                .with(httpBasic("john", "123456"))
                )
                .andDo(print())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        this.token = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @Test
    @DisplayName("Check findAllUsers (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllUsersSuccess() throws Exception {
        ResultActions response = this.mockMvc.perform(
                get(this.baseUrl + "/users")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Find All Success")))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    @DisplayName("Check findUserById (GET)")
    void testFindUserByIdSuccess() throws Exception {
        ResultActions response = this.mockMvc.perform(
                get(this.baseUrl + "/users/1")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Find One Success")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.username", is("john")))
        ;
    }

    @Test
    @DisplayName("Check findUserById with non-existent id (GET)")
    void testFindUserByIdNotFound() throws Exception {
        ResultActions response = this.mockMvc.perform(
                get(this.baseUrl + "/users/0")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND)))
                .andExpect(jsonPath("$.message", is("Could not find user with Id 0 :(")))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @Test
    @DisplayName("Check addUser with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddUserSuccess() throws Exception {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setUsername("lily");
        hogwartsUser.setPassword("123456");
        hogwartsUser.setEnabled(true);
        hogwartsUser.setRoles("admin user"); // The delimiter is space.

        String json = this.objectMapper.writeValueAsString(hogwartsUser);

        ResultActions response = this.mockMvc.perform(
                post(this.baseUrl + "/users")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        ResultActions response2 = this.mockMvc.perform(
                get(this.baseUrl + "/users")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Add Success")))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username", is("lily")))
                .andExpect(jsonPath("$.data.enabled", is(true)))
                .andExpect(jsonPath("$.data.roles", is("admin user")))
        ;

        response2.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Find All Success")))
                .andExpect(jsonPath("$.data", hasSize(4)))
        ;
    }

    @Test
    @DisplayName("Check addUser with invalid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddUserErrorWithInvalidInput() throws Exception {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setUsername(""); // Username is not provided.
        hogwartsUser.setPassword(""); // Password is not provided.
        hogwartsUser.setRoles(""); // Roles field is not provided.

        String json = this.objectMapper.writeValueAsString(hogwartsUser);

        ResultActions response = this.mockMvc.perform(
                post(this.baseUrl + "/users")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        ResultActions response2 = this.mockMvc.perform(
                get(this.baseUrl + "/users")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(INVALID_ARGUMENT)))
                .andExpect(jsonPath("$.message", is("Provided arguments are invalid, see data for details.")))
                .andExpect(jsonPath("$.data.username", is("username is required.")))
                .andExpect(jsonPath("$.data.password", is("password is required.")))
                .andExpect(jsonPath("$.data.roles", is("roles are required.")))
        ;

        response2.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Find All Success")))
                .andExpect(jsonPath("$.data", hasSize(3)))
        ;
    }

    @Test
    @DisplayName("Check updateUser with valid input (PUT)")
    void testUpdateUserSuccess() throws Exception {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setUsername("tom123"); // Username is changed. It was tom.
        hogwartsUser.setEnabled(false);
        hogwartsUser.setRoles("user");

        String json = this.objectMapper.writeValueAsString(hogwartsUser);

        ResultActions response = this.mockMvc.perform(
                put(this.baseUrl + "/users/3")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Update Success")))
                .andExpect(jsonPath("$.data.id", is(3)))
                .andExpect(jsonPath("$.data.username", is("tom123")))
                .andExpect(jsonPath("$.data.enabled", is(false)))
                .andExpect(jsonPath("$.data.roles", is("user")))
        ;
    }

    @Test
    @DisplayName("Check updateUser with non-existent id (PUT)")
    void testUpdateUserErrorWithNonExistentId() throws Exception {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setId(0); // This id does not exist in the database.
        hogwartsUser.setUsername("john123"); // Username is changed.
        hogwartsUser.setEnabled(true);
        hogwartsUser.setRoles("admin user");

        String json = this.objectMapper.writeValueAsString(hogwartsUser);

        ResultActions response = this.mockMvc.perform(
                put(this.baseUrl + "/users/0")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND)))
                .andExpect(jsonPath("$.message", is("Could not find user with Id 0 :(")))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @Test
    @DisplayName("Check updateUser with invalid input (PUT)")
    void testUpdateUserErrorWithInvalidInput() throws Exception {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setUsername(""); // Update username is empty.
        hogwartsUser.setRoles(""); // Update roles is empty.

        String json = this.objectMapper.writeValueAsString(hogwartsUser);

        ResultActions response = this.mockMvc.perform(
                put(this.baseUrl + "/users/3")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(INVALID_ARGUMENT)))
                .andExpect(jsonPath("$.message", is("Provided arguments are invalid, see data for details.")))
                .andExpect(jsonPath("$.data.username", is("username is required.")))
                .andExpect(jsonPath("$.data.roles", is("roles are required.")))
        ;
    }

    @Test
    @DisplayName("Check deleteUser with valid input (DELETE)")
    void testDeleteUserSuccess() throws Exception {
        ResultActions response = this.mockMvc.perform(
                delete(this.baseUrl + "/users/3")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Delete Success")))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @Test
    @DisplayName("Check deleteUser with non-existent id (DELETE)")
    void testDeleteUserErrorWithNonExistentId() throws Exception {
        ResultActions response = this.mockMvc.perform(
                delete(this.baseUrl + "/users/0")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND)))
                .andExpect(jsonPath("$.message", is("Could not find user with Id 0 :(")))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @Test
    @DisplayName("Check deleteUser with insufficient permission (DELETE)")
    void testDeleteUserNoAccessAsRoleUser() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                post(this.baseUrl + "/users/login")
                        .with(httpBasic("eric", "654321"))
        ).andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        String ericToken = "Bearer " + json.getJSONObject("data").getString("token");

        ResultActions response = this.mockMvc.perform(
                delete(this.baseUrl + "/users/2")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, ericToken)
        );

        ResultActions response2 = this.mockMvc.perform(
                get(this.baseUrl + "/users")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(FORBIDDEN)))
                .andExpect(jsonPath("$.message", is("No permission.")))
                .andExpect(jsonPath("$.data", is("Access Denied")));
        response2.andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Find All Success")))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].username", is("john")));
    }

}