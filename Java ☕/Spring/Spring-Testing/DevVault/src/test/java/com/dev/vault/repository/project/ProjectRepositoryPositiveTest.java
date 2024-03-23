package com.dev.vault.repository.project;

import com.dev.vault.model.project.Project;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ProjectRepositoryPositiveTest {

    private @Autowired UserRepository userRepository;
    private @Autowired ProjectRepository projectRepository;

    private User mockUser1, mockUser2;
    private Project mockProject1, mockProject2, mockProject3;

    @BeforeEach
    public void setup() {
        mockUser1 = User.builder().userId(1L).email("test@example.com").build();
        mockUser2 = User.builder().userId(2L).email("test2@example.com").build();

        mockProject1 = Project.builder().projectId(1L).projectName("Java Project 1").build();
        mockProject2 = Project.builder().projectId(2L).projectName("Spring Project 2").build();
        mockProject3 = Project.builder().projectId(3L).projectName("Java SB Project 3").build();

        mockProject1.setLeader(mockUser1); /* rel */
        mockProject2.setLeader(mockUser2); /* rel */
    }

    @Test
    @DisplayName("➕ Find Projects By Project Name Containing Operation")
    public void givenProjectName_WhenFindByProjectNameContaining_ThenReturnListOfSimilarProjectsWithGivenName() {
        // given (precondition or setup)
        userRepository.saveAll(List.of(mockUser1, mockUser2));
        projectRepository.saveAll(List.of(mockProject1, mockProject2, mockProject3));

        // when (action occurs / action or the behaviour that we are going to test)
        List<Project> foundJavaProjects = projectRepository.findByProjectNameContaining("Java");

        // then (verify the output)
        Project project1 = foundJavaProjects.get(0); // equivalent to mockProject1
        Project project3 = foundJavaProjects.get(1); // equivalent to mockProject3

        assertEquals("must find 2 items", 2, foundJavaProjects.size());
        assertTrue("must have project1 object", foundJavaProjects.contains(project1));
        assertTrue("must have project3 object", foundJavaProjects.contains(project3));
        assertThat(foundJavaProjects)
                .as("Projects containing 'Java'")
                .hasSize(2)
                .contains(project1)
                .contains(project3);
    }


    @Test
    @DisplayName("➕ Find Optional Of Project By ProjectName AllIgnoreCase Operation")
    public void givenProjectNameIgnoreCase_WhenFindByProjectNameAllIgnoreCase_ThenReturnOptionalOfFoundProject() {
        // given (precondition or setup)
        userRepository.saveAll(List.of(mockUser1, mockUser2));
        projectRepository.saveAll(List.of(mockProject1, mockProject2, mockProject3));

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Project> foundProject1Optional = projectRepository.findByProjectNameAllIgnoreCase("jAvA pRoJeCt 1");

        // then (verify the output)
        assertThat(foundProject1Optional)
                .as("Project1 ignore case search present")
                .isPresent();

        foundProject1Optional.ifPresent(
                project -> assertThat(project.getProjectName())
                        .as("Project1 ignore case search matches mockProject1")
                        .isEqualTo(mockProject1.getProjectName())
        );
    }

}