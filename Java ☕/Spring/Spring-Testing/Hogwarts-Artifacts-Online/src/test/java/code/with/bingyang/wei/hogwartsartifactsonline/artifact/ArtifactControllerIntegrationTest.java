package code.with.bingyang.wei.hogwartsartifactsonline.artifact;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.dto.ArtifactDTO;
import code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.NOT_FOUND;
import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.SUCCESS;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Tag("integration")
@DisplayName("Integration test for Artifact api endpoints")
@SpringBootTest
@AutoConfigureMockMvc
class ArtifactControllerIntegrationTest {

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectM  apper objectMapper;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                        post(this.baseUrl + "/users/login")
                                .with(httpBasic("john", "123456"))
                )
                .andDo(print())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        this.token = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @Test
    @DisplayName("Check findAllArtifacts (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
        // Reset H2 database before calling this test case.
    void testFindAllArtifactsSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Check findArtifactById (GET)")
    void testFindArtifactByIdSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/artifacts/12334545").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("12334545"))
                .andExpect(jsonPath("$.data.name").value("The Sword Of Gryffindor"));
    }

    @Test
    @DisplayName("Check findArtifactById with non-existent id (GET)")
    void testFindArtifactByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1250808601744904199").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check addArtifact with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddArtifactSuccess() throws Exception {
        Artifact a = new Artifact();
        a.setName("Remembrall");
        a.setDescription("A Remembrall was a magical large marble-sized glass ball that contained smoke which turned red when its owner or user had forgotten something. It turned clear once whatever was forgotten was remembered.");
        a.setImageUrl("ImageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(post(this.baseUrl + "/artifacts").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Remembrall"))
                .andExpect(jsonPath("$.data.description").value("A Remembrall was a magical large marble-sized glass ball that contained smoke which turned red when its owner or user had forgotten something. It turned clear once whatever was forgotten was remembered."))
                .andExpect(jsonPath("$.data.imageUrl").value("ImageUrl"));
        this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));
    }

    @Test
    @DisplayName("Check addArtifact with invalid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddArtifactErrorWithInvalidInput() throws Exception {
        Artifact a = new Artifact();
        a.setName(""); // Name is not provided.
        a.setDescription(""); // Description is not provided.
        a.setImageUrl(""); // ImageUrl is not provided.

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(post(this.baseUrl + "/artifacts").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required."))
                .andExpect(jsonPath("$.data.description").value("description is required."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required."));
        this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Check updateArtifact with valid input (PUT)")
    void testUpdateArtifactSuccess() throws Exception {
        Artifact a = new Artifact();
        a.setId("28493232");
        a.setName("Updated artifact name");
        a.setDescription("Updated description");
        a.setImageUrl("Updated imageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        ResultActions response = this.mockMvc.perform(put(
                this.baseUrl + "/artifacts/28493232")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, this.token)
        );

        response.andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("28493232"))
                .andExpect(jsonPath("$.data.name").value("Updated artifact name"))
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.imageUrl").value("Updated imageUrl"));
    }

    @Test
    void testUpdateArtifactNotFound() throws Exception {
        ArtifactDTO artifactDTO = new ArtifactDTO(
                "blah",
                "blah",
                "blah",
                "blah",
                null
        );
        String json = this.objectMapper.writeValueAsString(artifactDTO);

        ResultActions response = this.mockMvc.perform(
                put("/api/v1/artifacts/123456")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND)))
                .andExpect(jsonPath("$.message", is("Could not find artifact with Id 123456 :(")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check updateArtifact with non-existent id (PUT)")
    void testUpdateArtifactErrorWithNonExistentId() throws Exception {
        Artifact a = new Artifact();
        a.setId("1250808601744904199"); // This id does not exist in the database.
        a.setName("Updated artifact name");
        a.setDescription("Updated description");
        a.setImageUrl("Updated imageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        ResultActions response = this.mockMvc.perform(
                put(this.baseUrl + "/artifacts/1250808601744904199")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check updateArtifact with invalid input (PUT)")
    void testUpdateArtifactErrorWithInvalidInput() throws Exception {
        Artifact a = new Artifact();
        a.setId("12334545"); // Valid id
        a.setName(""); // Updated name is empty.
        a.setDescription(""); // Updated description is empty.
        a.setImageUrl(""); // Updated imageUrl is empty.

        String json = this.objectMapper.writeValueAsString(a);

        ResultActions response = this.mockMvc.perform(
                put(this.baseUrl + "/artifacts/12334545")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        ResultActions response2 = this.mockMvc.perform(
                get(this.baseUrl + "/artifacts/12334545")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required."))
                .andExpect(jsonPath("$.data.description").value("description is required."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required."));

        response2.andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("12334545"))
                .andExpect(jsonPath("$.data.name").value("The Sword Of Gryffindor"));
    }

    @Test
    @DisplayName("Check deleteArtifact with valid input (DELETE)")
    void testDeleteArtifactSuccess() throws Exception {
        ResultActions response = this.mockMvc.perform(
                delete("/api/v1/artifacts/12334545")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check deleteArtifact with non-existent id (DELETE)")
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        ResultActions response = this.mockMvc.perform(
                delete("/api/v1/artifacts/000")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, this.token)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 000 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}