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
import static org.springframework.test.util.AssertionErrors.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProjectRepositoryNegativeTest {

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
    @DisplayName("➖ SHOULD NOT Find Projects By Project Name Containing Operation")
    public void givenNotContainingProjectNames_WhenFindByProjectNameContaining_ThenShouldNotReturnProjects() {
        // given
        userRepository.saveAll(List.of(mockUser1, mockUser2));
        projectRepository.saveAll(List.of(mockProject1, mockProject2, mockProject3));

        // when
        List<Project> foundSpringProjects = projectRepository.findByProjectNameContaining("Spring");

        // then
        Project project2 = foundSpringProjects.get(0); // equivalent to mockProject2

        assertEquals("must find 1 item", 1, foundSpringProjects.size());
        assertTrue("must have project2 object", foundSpringProjects.contains(project2));
        assertFalse("must NOT have project1 object", foundSpringProjects.contains(mockProject1));
        assertFalse("must NOT have project3 object", foundSpringProjects.contains(mockProject2));

        assertThat(foundSpringProjects)
                .as("Projects containing 'Spring'")
                .hasSize(1)
                .contains(project2);
    }


    @Test
    @DisplayName("➖ SHOULD NOT Find Optional Of Project By ProjectName AllIgnoreCase Operation")
    public void givenNonExistentProjectName_WhenFindByProjectNameAllIgnoreCase_ThenReturnEmptyOptional() {
        // given (precondition or setup)
        userRepository.saveAll(List.of(mockUser1, mockUser2));
        projectRepository.saveAll(List.of(mockProject1, mockProject2, mockProject3));

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Project> notExistentProjectOptional = projectRepository.findByProjectNameAllIgnoreCase("Non Existent Project");

        // then (verify the output)
        assertThat(notExistentProjectOptional)
                .as("Non existent project search should be empty")
                .isEmpty();
    }

}
