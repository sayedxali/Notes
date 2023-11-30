package code.with.bingyang.wei.hogwartsartifactsonline.artifact;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.converter.ArtifactDTOToArtifactConverter;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.dto.ArtifactDTO;
import code.with.bingyang.wei.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.NOT_FOUND;
import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.SUCCESS;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//@SpringBootTest
//@AutoConfigureMockMvc
@WebMvcTest(value = ArtifactController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ArtifactControllerTest {

    private @MockBean ArtifactService artifactService;
    private @MockBean ArtifactDTOToArtifactConverter artifactDTOToArtifactConverter;
    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;


    private List<Artifact> artifacts;
    @Value("${api.endpoint.base-url}")
    private String BASE_URL;
    private static final String ARTIFACT_NOT_FOUND = "artifact";

    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();
        Artifact artifact1 = Artifact.builder().id("12334545").name("The Sword Of Gryffindor").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
        Artifact artifact2 = Artifact.builder().id("45632112").name("Resurrection Stone").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
        Artifact artifact3 = Artifact.builder().id("28493232").name("The Sword Of Gryffindor 2").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
        Artifact artifact4 = Artifact.builder().id("31242342").name("The Sword Of Gryffindor 3").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();

        this.artifacts.add(artifact1);
        this.artifacts.add(artifact2);
        this.artifacts.add(artifact3);
        this.artifacts.add(artifact4);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindArtifactByIdSuccess() throws Exception {
        // Given
        given(this.artifactService.findById("12334545"))
                .willReturn(this.artifacts.get(0));

        // When
        ResultActions response = this.mockMvc.perform(
                get(BASE_URL + "/artifacts/12334545")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("12334545"))
                .andExpect(jsonPath("$.data.name").value("The Sword Of Gryffindor"));
    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {
        // Given
        given(this.artifactService.findById("12334545"))
                .willThrow(new ObjectNotFoundException(ARTIFACT_NOT_FOUND, "12334545"));

        // When
        ResultActions response = this.mockMvc.perform(
                get(BASE_URL + "/artifacts/12334545")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 12334545 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {
        // Given
        given(this.artifactService.findAll())
                .willReturn(this.artifacts);

        // When
        ResultActions response = this.mockMvc.perform(
                get("/api/v1/artifacts")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", hasSize(this.artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("12334545"))
                .andExpect(jsonPath("$.data[0].name").value("The Sword Of Gryffindor"))
                .andExpect(jsonPath("$.data[1].id").value("45632112"))
                .andExpect(jsonPath("$.data[1].name").value("Resurrection Stone"));
    }

    @Test
    void testAddArtifactSuccess() throws Exception {
        // Given
        ArtifactDTO artifactDTO = new ArtifactDTO(
                "1234567890123456789",
                "Rememberall",
                "Description",
                "ImageUrl",
                null
        );
        String json = this.objectMapper.writeValueAsString(artifactDTO);

        Artifact savedArtifact = Artifact.builder()
                .id("1234567890123456789")
                .name("RememberAll")
                .description("Description")
                .imageUrl("ImageUrl")
                .build();

        given(this.artifactService.save(Mockito.any(Artifact.class)))
                .willReturn(savedArtifact);
        given(this.artifactDTOToArtifactConverter.convert(artifactDTO))
                .willReturn(savedArtifact);

        // When
        ResultActions response = this.mockMvc.perform(
                post("/api/v1/artifacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {
        // Given
        ArtifactDTO artifactDTO = new ArtifactDTO(
                "123456",
                "Rememberall",
                "Description",
                "ImageUrl",
                null
        );
        String json = this.objectMapper.writeValueAsString(artifactDTO);

        Artifact updatedArtifact = Artifact.builder()
                .id(artifactDTO.id())
                .name("Updated Rememberall")
                .description("Updated Description.")
                .imageUrl("Updated ImageUrl.")
                .build();

        Artifact updateArtifactRequest = this.artifactDTOToArtifactConverter.convert(artifactDTO);
        given(this.artifactService.update("123456", updateArtifactRequest))
                .willReturn(updatedArtifact);

        // When
        ResultActions response = this.mockMvc.perform(
                put("/api/v1/artifacts/123456")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Update Success")))
                .andExpect(jsonPath("$.data.id", is("123456")))
                .andExpect(jsonPath("$.data.name", is(updatedArtifact.getName())))
                .andExpect(jsonPath("$.data.description", is(updatedArtifact.getDescription())))
                .andExpect(jsonPath("$.data.imageUrl", is(updatedArtifact.getImageUrl())));
    }

    @Test
    void testUpdateArtifactNotFound() throws Exception {
        // Given
        ArtifactDTO artifactDTO = new ArtifactDTO(
                "123456",
                "Rememberall",
                "Description",
                "ImageUrl",
                null
        );
        String json = this.objectMapper.writeValueAsString(artifactDTO);

        Artifact updateArtifactRequest = this.artifactDTOToArtifactConverter.convert(artifactDTO);
        given(this.artifactService.update("123456", updateArtifactRequest))
                .willThrow(new ObjectNotFoundException(ARTIFACT_NOT_FOUND, "123456"));

        // When
        ResultActions response = this.mockMvc.perform(
                put("/api/v1/artifacts/123456")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND)))
                .andExpect(jsonPath("$.message", is("Could not find artifact with Id 123456 :(")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactSuccess() throws Exception {
        // Given
        doNothing().when(this.artifactService).delete("1234567890123456789");

        // When
        ResultActions response = this.mockMvc.perform(
                delete("/api/v1/artifacts/1234567890123456789")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException(ARTIFACT_NOT_FOUND, "1234567890123456789"))
                .when(this.artifactService).delete("1234567890123456789");

        // When
        ResultActions response = this.mockMvc.perform(
                delete("/api/v1/artifacts/1234567890123456789")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1234567890123456789 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}