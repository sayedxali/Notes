package code.with.bingyang.wei.hogwartsartifactsonline.wizard;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.Artifact;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.ArtifactRepository;
import code.with.bingyang.wei.hogwartsartifactsonline.artifact.utils.IdWorker;
import code.with.bingyang.wei.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    private @InjectMocks WizardService wizardService;
    private @Mock WizardRepository wizardRepository;
    private @Mock ArtifactRepository artifactRepository;
    private @Mock IdWorker idWorker;


    private List<Wizard> wizards;
    private List<Artifact> artifacts;

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
    void testFindByIdSuccess() {
        // Given
        /*
            "id": 1,
            "name": "Albus Dumbledore"
        */
        Artifact artifact1 = Artifact.builder()
                .id("1250808601744904192")
                .name("Invisibility Cloak")
                .description("An invisibility cloak is used to moke the wearer invisible.")
                .imageUrl("ImageUrl")
                .build();
        Artifact artifact2 = Artifact.builder()
                .id("123232601744904192")
                .name("Visible Cloak")
                .description("Description.")
                .imageUrl("ImageUrl")
                .build();
        Wizard wizard = Wizard.builder()
                .id(1)
                .name("Albus Dumbledore")
                .artifacts(List.of(artifact1, artifact2))
                .build();

        given(wizardRepository.findById(1))
                .willReturn(Optional.of(wizard));

        // When
        Wizard foundWizard = this.wizardService.findById(1);

        // Then
        assertThat(foundWizard.getId()).isEqualTo(wizard.getId());
        assertThat(foundWizard.getName()).isEqualTo(wizard.getName());

        verify(wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        given(wizardRepository.findById(1))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> this.wizardService.findById(1));

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 1 :(");

        verify(wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindAllSuccess() {
        // Given
        given(wizardRepository.findAll())
                .willReturn(this.wizards);

        // When
        List<Wizard> actualWizards = this.wizardService.findAll();

        // Then
        assertThat(actualWizards).hasSize(this.wizards.size());

        verify(wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        // Given
        Wizard newWizard = Wizard.builder().name("New Wizard").build();

        given(wizardRepository.save(newWizard))
                .willReturn(newWizard);

        // When
        Wizard savedWizard = this.wizardService.save(newWizard);

        // Then
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());

        verify(wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Wizard wizard = new Wizard(1, "Harry Potter", null);
        given(wizardRepository.findById(1))
                .willReturn(Optional.of(wizard));

        wizard.setName("Ginger");
        wizard.setArtifacts(this.artifacts);
        given(wizardRepository.save(wizard))
                .willReturn(wizard);

        // When
        Wizard updatedWizard = this.wizardService.update(1, wizard);

        // Then
        assertThat(updatedWizard.getId()).isEqualTo(wizard.getId());
        assertThat(updatedWizard.getName()).isEqualTo(wizard.getName());
        assertThat(updatedWizard.getNumberOfArtifacts()).isEqualTo(wizard.getNumberOfArtifacts());

        verify(wizardRepository, times(1)).findById(1);
        verify(wizardRepository, times(1)).save(wizard);
    }

    @Test
    void testUpdateNotFound() {
        // Given
        Wizard wizard = new Wizard(1, "Harry Potter", null);
        given(wizardRepository.findById(1))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> this.wizardService.update(1, wizard));

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 1 :(");

        verify(wizardRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteSuccess() {
        // Given
        Integer wizardId = 1;
        given(this.wizardRepository.findById(wizardId))
                .willReturn(Optional.ofNullable(this.wizards.get(0)));
        doNothing()
                .when(this.wizardRepository).deleteById(wizardId);

        // When
        this.wizardService.delete(wizardId);

        // Then
        verify(this.wizardRepository, times(1)).findById(wizardId);
        verify(this.wizardRepository, times(1)).deleteById(wizardId);
    }

    @Test
    void testDeleteNotFound() {
        // Given
        Integer wizardId = 1;
        given(this.wizardRepository.findById(wizardId))
                .willReturn(Optional.empty());

        // When
        assertThrows(
                ObjectNotFoundException.class,
                () -> this.wizardService.delete(wizardId)
        );

        // Then
        verify(this.wizardRepository, times(1)).findById(wizardId);
    }

    @Test
    void testAssignArtifactSuccess() {
        // Given
        Artifact artifact = new Artifact();
        artifact.setId("123");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("Description");
        artifact.setImageUrl("ImageUrl");

        Wizard wizard1 = new Wizard();
        wizard1.setId(2);
        wizard1.setName("Harry Potter");
        wizard1.addArtifact(artifact);

        // we want to assign artifact on this wizard
        Wizard wizard2 = new Wizard();
        wizard2.setId(3);
        wizard2.setName("Neville Longbottom");

        given(this.artifactRepository.findById("123"))
                .willReturn(Optional.of(artifact));
        given(this.wizardRepository.findById(3))
                .willReturn(Optional.of(wizard2));

        // When
        this.wizardService.assignArtifact(3, "123");

        // Then
        assertThat(artifact.getOwner().getId()).isEqualTo(3);
        assertThat(wizard2.getArtifacts()).contains(artifact);
    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() {
        // Given
        Artifact artifact = new Artifact();
        artifact.setId("123");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("Description");
        artifact.setImageUrl("ImageUrl");

        Wizard wizard1 = new Wizard();
        wizard1.setId(2);
        wizard1.setName("Harry Potter");
        wizard1.addArtifact(artifact);

        given(this.artifactRepository.findById("123"))
                .willReturn(Optional.of(artifact));
        given(this.wizardRepository.findById(3))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = assertThrows(
                ObjectNotFoundException.class,
                () -> this.wizardService.assignArtifact(3, "123")
        );

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 3 :(");
        assertThat(artifact.getOwner().getId()).isEqualTo(2);
    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() {
        // Given
        given(this.artifactRepository.findById("123"))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = assertThrows(
                ObjectNotFoundException.class,
                () -> this.wizardService.assignArtifact(3, "123")
        );

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 123 :(");
    }

}