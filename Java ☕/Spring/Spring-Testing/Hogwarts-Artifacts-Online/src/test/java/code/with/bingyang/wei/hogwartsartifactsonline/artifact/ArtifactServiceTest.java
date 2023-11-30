package code.with.bingyang.wei.hogwartsartifactsonline.artifact;

import code.with.bingyang.wei.hogwartsartifactsonline.artifact.utils.IdWorker;
import code.with.bingyang.wei.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import code.with.bingyang.wei.hogwartsartifactsonline.wizard.Wizard;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    private @InjectMocks ArtifactService artifactService;

    private @Mock ArtifactRepository artifactRepository;
    private @Mock IdWorker idWorker;

    private List<Artifact> artifacts;

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
    void testFindByIdSuccess() {
        // Given. Arrange inputs and targets. Define the behavior of Mock object artifactRepository.
        /*
            "id": "1250808601744904192",
            "name": "Invisibility Cloak",
            "description":"An invisibility cloak is used to moke the wearer invisible.",
            "imageUrl":"ImageUrl"
        */
        Artifact artifact = Artifact.builder()
                .id("1250808601744904192")
                .name("Invisibility Cloak")
                .description("An invisibility cloak is used to moke the wearer invisible.")
                .imageUrl("ImageUrl")
                .build();
        Wizard wizard = Wizard.builder()
                .id(2)
                .name("Harry Potter")
                .build();

        artifact.setOwner(wizard);

        given(artifactRepository.findById("1250808601744904192"))
                .willReturn(Optional.of(artifact)); // Defines the behavior of the mock object.

        // When. Act on the target behavior. When steps should cover the method to be tested.
        Artifact returnedArtifact = artifactService.findById("1250808601744904192");

        // Then. Assert expected outcomes.
        assertThat(returnedArtifact.getId()).isEqualTo(artifact.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(artifact.getImageUrl());
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        given(artifactRepository.findById(anyString()))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> artifactService.findById("3824938247983"));

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 3824938247983 :(");

        verify(artifactRepository, times(1)).findById("3824938247983");
    }

    @Test
    void testFindAllSuccess() {
        // Given
        given(artifactRepository.findAll())
                .willReturn(this.artifacts);

        // When
        List<Artifact> actualArtifacts = artifactService.findAll();

        // Then
        assertThat(actualArtifacts).hasSize(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        // Given
        Artifact newArtifact = Artifact.builder()
                .name("Artifact 5")
                .description("Description...")
                .imageUrl("ImageUrl...")
                .build();

        given(idWorker.nextId())
                .willReturn(123456L);
        given(artifactRepository.save(newArtifact))
                .willReturn(newArtifact);

        // When
        Artifact savedArtifact = this.artifactService.save(newArtifact);

        // Then
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());

        verify(artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Artifact artifact = new Artifact("123456", "Artifact", "Description...", "ImageUrl...", null);
        given(artifactRepository.findById("123456"))
                .willReturn(Optional.of(artifact));

        artifact.setId("123456");
        artifact.setName("Updated Artifact");
        artifact.setDescription("Description");
        artifact.setImageUrl("ImageUrl");

        given(artifactRepository.save(artifact))
                .willReturn(artifact);

        // When
        Artifact updatedArtifactResponse = this.artifactService.update("123456", artifact);

        // Then
        assertThat(updatedArtifactResponse.getId()).isEqualTo("123456");
        assertThat(updatedArtifactResponse.getName()).isEqualTo(artifact.getName());
        assertThat(updatedArtifactResponse.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(updatedArtifactResponse.getImageUrl()).isEqualTo(artifact.getImageUrl());

        verify(artifactRepository, times(1)).findById("123456");
        verify(artifactRepository, times(1)).save(artifact);
    }

    @Test
    void testUpdateNotFound() {
        // Given
        Artifact artifact = new Artifact("123456", "Artifact", "Description...", "ImageUrl...", null);
        given(artifactRepository.findById("123456"))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> this.artifactService.update("123456", artifact));

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 123456 :(");

        verify(artifactRepository, times(1)).findById("123456");
    }

    @Test
    void testDeleteSuccess() {
        // Given
        Artifact artifact = Artifact.builder()
                .id("1234567890123456789")
                .name("Invisibility Cloak")
                .description("Description")
                .imageUrl("ImageUrl")
                .build();
        given(artifactRepository.findById("1234567890123456789"))
                .willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("1234567890123456789");

        // When
        artifactService.delete("1234567890123456789");

        // Then
        verify(artifactRepository, times(1)).findById("1234567890123456789");
        verify(artifactRepository, times(1)).deleteById("1234567890123456789");
    }

    @Test
    void testDeleteNotFound() {
        // Given
        given(artifactRepository.findById("1234567890123456789"))
                .willReturn(Optional.empty());

        // When
        assertThrows(
                ObjectNotFoundException.class,
                () -> artifactService.delete("1234567890123456789")
        );

        // Then
        verify(artifactRepository, times(1)).findById("1234567890123456789");
    }

}