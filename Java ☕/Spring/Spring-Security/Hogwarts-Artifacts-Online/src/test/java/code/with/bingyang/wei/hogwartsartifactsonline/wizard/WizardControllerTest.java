package code.with.bingyang.wei.hogwartsartifactsonline.wizard;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.Artifact;
import code.with.bingyang.wei.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.converter.WizardDTOToWizardConverter;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.dto.WizardDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = WizardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class WizardControllerTest {

    private @MockBean WizardService wizardService;
    private @MockBean WizardDTOToWizardConverter wizardDTOToWizardConverter;

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private List<Wizard> wizards;
    private List<Artifact> artifacts;

    @Value("${api.endpoint.base-url}")
    private String BASE_URL;

    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();
        Artifact artifact1 = Artifact.builder().id("1").name("The Sword Of Gryffindor").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
        Artifact artifact2 = Artifact.builder().id("2").name("Resurrection Stone").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
        Artifact artifact3 = Artifact.builder().id("3").name("The Sword Of Gryffindor 2").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();
        Artifact artifact4 = Artifact.builder().id("4").name("The Sword Of Gryffindor 3").description("A goblin-made sword adorned with large rubies on the pommel.").imageUrl("ImageUrl").build();

        this.artifacts.add(artifact1);
        this.artifacts.add(artifact2);
        this.artifacts.add(artifact3);
        this.artifacts.add(artifact4);

        this.wizards = new ArrayList<>();
        Wizard wizard1 = Wizard.builder().id(1).name("Albus Dumbledore").build();
        Wizard wizard2 = Wizard.builder().id(2).name("Harry Potter").build();
        Wizard wizard3 = Wizard.builder().id(3).name("Voldemort").build();

        this.wizards.add(wizard1);
        this.wizards.add(wizard2);
        this.wizards.add(wizard3);

        wizard1.setArtifacts(List.of(artifact1, artifact2));
        wizard2.setArtifacts(List.of(artifact3, artifact4));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindWizardByIdSuccess() throws Exception {
        // Given
        given(this.wizardService.findById(1))
                .willReturn(this.wizards.get(0));

        // When
        ResultActions response = this.mockMvc.perform(
                get(this.BASE_URL + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Find One Success")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Albus Dumbledore")))
                .andExpect(jsonPath("$.data.numberOfArtifacts", is(2)));
    }

    @Test
    void testFindWizardByIdNotFound() throws Exception {
        // Given
        int wizardId = 1;
        given(this.wizardService.findById(wizardId))
                .willThrow(new ObjectNotFoundException("wizard", wizardId));

        // When
        ResultActions response = this.mockMvc.perform(
                get(this.BASE_URL + "/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND)))
                .andExpect(jsonPath("$.message", is("Could not find wizard with Id " + wizardId + " :(")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllWizardSuccess() throws Exception {
        // Given
        given(this.wizardService.findAll())
                .willReturn(this.wizards);

        // When
        ResultActions response = this.mockMvc.perform(
                get(this.BASE_URL + "/wizards")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Find All Success")))
                .andExpect(jsonPath("$.data", hasSize(this.wizards.size())))
                .andExpect(jsonPath("$.data[0].id", is(this.wizards.get(0).getId())))
                .andExpect(jsonPath("$.data[0].name", is(this.wizards.get(0).getName())))
                .andExpect(jsonPath("$.data[1].id", is(this.wizards.get(1).getId())))
                .andExpect(jsonPath("$.data[1].name", is(this.wizards.get(1).getName())));
    }

    @Test
    void testAddWizardSuccess() throws Exception {
        // Given
        WizardDTO wizardDTO = new WizardDTO(1, "New Wizard", null);
        String json = this.objectMapper.writeValueAsString(wizardDTO);

        Wizard newWizard = Wizard.builder().id(1).name("New Wizard").build();

        given(this.wizardService.save(any(Wizard.class)))
                .willReturn(newWizard);
        given(this.wizardDTOToWizardConverter.convert(wizardDTO))
                .willReturn(newWizard);

        // When
        ResultActions response = this.mockMvc.perform(
                post(this.BASE_URL + "/wizards")
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
                .andExpect(jsonPath("$.data.name", is(newWizard.getName())));
    }

    @Test
    void testUpdateWizardSuccess() throws Exception {
        // Given
        int wizardId = 1;
        WizardDTO wizardDTO = new WizardDTO(wizardId, "HarryPotter", null);
        String json = this.objectMapper.writeValueAsString(wizardDTO);

        Wizard updatedWizard = new Wizard(wizardDTO.id(), wizardDTO.name(), null);

        Wizard updateArtifactRequest = this.wizardDTOToWizardConverter.convert(wizardDTO);
        given(this.wizardService.update(wizardId, updateArtifactRequest))
                .willReturn(updatedWizard);

        // When
        ResultActions response = this.mockMvc.perform(
                put(this.BASE_URL + "/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(true)))
                .andExpect(jsonPath("$.code", is(SUCCESS)))
                .andExpect(jsonPath("$.message", is("Update Success")))
                .andExpect(jsonPath("$.data.id", is(wizardId)))
                .andExpect(jsonPath("$.data.name", is(updatedWizard.getName())))
                .andExpect(jsonPath("$.data.numberOfArtifacts", is(updatedWizard.getNumberOfArtifacts())))
                .andExpect(jsonPath("$.data.numberOfArtifacts", is(0)));
    }

    @Test
    void testUpdateWizardNotFound() throws Exception {
        // Given
        int wizardId = 1;
        WizardDTO wizardDTO = new WizardDTO(wizardId, "HarryPotter", null);
        String json = this.objectMapper.writeValueAsString(wizardDTO);

        Wizard updateArtifactRequest = this.wizardDTOToWizardConverter.convert(wizardDTO);
        given(this.wizardService.update(wizardId, updateArtifactRequest))
                .willThrow(new ObjectNotFoundException("wizard", wizardId));

        // When
        ResultActions response = this.mockMvc.perform(
                put(this.BASE_URL + "/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag", is(false)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND)))
                .andExpect(jsonPath("$.message", is("Could not find wizard with Id " + wizardId + " :(")))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardSuccess() throws Exception {
        // Given
        int wizardId = 1;
        doNothing().when(this.wizardService).delete(wizardId);

        // When
        ResultActions response = this.mockMvc.perform(
                delete(this.BASE_URL + "/wizards/" + wizardId)
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
    void testDeleteWizardErrorWithNonExistentId() throws Exception {
        // Given
        int wizardId = 1;
        doThrow(new ObjectNotFoundException("wizard", wizardId))
                .when(this.wizardService)
                .delete(wizardId);

        // When
        ResultActions response = this.mockMvc.perform(
                delete(this.BASE_URL + "/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id " + wizardId + " :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactSuccess() throws Exception {
        // Given
        doNothing()
                .when(this.wizardService)
                .assignArtifact(2, "123");

        // When
        ResultActions response = this.mockMvc.perform(
                put(this.BASE_URL + "/wizards/2/artifacts/123")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("wizard", 5))
                .when(this.wizardService)
                .assignArtifact(5, "123");

        // When
        ResultActions response = this.mockMvc.perform(
                put(this.BASE_URL + "/wizards/5/artifacts/123")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("artifact", "000"))
                .when(this.wizardService)
                .assignArtifact(5, "000");

        // When
        ResultActions response = this.mockMvc.perform(
                put(this.BASE_URL + "/wizards/5/artifacts/000")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        response.andDo(print())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 000 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }

}