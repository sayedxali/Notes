package com.dev.vault.service.module.project;

import com.dev.vault.helper.exception.ResourceNotFoundException;
import com.dev.vault.helper.payload.group.SearchResponse;
import com.dev.vault.model.project.Project;
import com.dev.vault.model.project.ProjectMembers;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.project.ProjectMembersRepository;
import com.dev.vault.repository.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchProjectServiceTest {

    private @InjectMocks SearchProjectServiceImpl searchProjectService;

    //<editor-fold desc="mocks to be injected in `SearchProjectServiceImpl` class">
    private @Mock ProjectRepository projectRepository;
    private @Mock ProjectMembersRepository projectMembersRepository;
    //</editor-fold>

    //<editor-fold desc="objects to be created before each method">
    private User user;
    private Project project1, project2, project3;
    private ProjectMembers projectMembers1, projectMembers2, projectMembers3;
    //</editor-fold>

    @BeforeEach
    public void setup() {
        // Arrange
        user = new User();
        user.setUsername("TestUser");

        project1 = new Project();
        project1.setProjectName("Java Project");
        project1.setLeader(user); /* rel */

        project2 = new Project();
        project2.setProjectName("Spring Boot Project");
        project2.setLeader(user); /* rel */

        project3 = new Project();
        project3.setProjectName("Android With Java Project");
        project3.setLeader(user); /* rel */

        projectMembers1 = new ProjectMembers(); /* rel */
        projectMembers1.setUser(user);
        projectMembers1.setProject(project1);

        projectMembers2 = new ProjectMembers(); /* rel */
        projectMembers2.setUser(user);
        projectMembers2.setProject(project2);

        projectMembers3 = new ProjectMembers(); /* rel */
        projectMembers3.setUser(user);
        projectMembers3.setProject(project3);
    }


    /**
     * Test: List All Projects Operation
     */
    @Test
    @DisplayName("➕ List All Projects Operation")
    public void givenNothing_WhenListAllProjects_ThenReturnListOfSearchResponse() {
        // Step 1: Create User and Project Objects (consider moving to a helper method)

        // given (precondition or setup)
        when(projectRepository.findAll())
                .thenReturn(List.of(project1, project2));
        when(projectMembersRepository.findByProject(project1))
                .thenReturn(List.of(projectMembers1));
        when(projectMembersRepository.findByProject(project2))
                .thenReturn(List.of(projectMembers2));

        // when (action occurs / action or the behaviour that we are going to test)
        List<SearchResponse> searchResponseList = searchProjectService.listAllProjects();

        // then (verify the output)
        SearchResponse response = searchResponseList.get(0);

        assertThat(searchResponseList).hasSize(2);
        assertThat(searchResponseList.get(0).getProjectName())
                .as("Java Project (Project1)")
                .isEqualTo(project1.getProjectName());
        assertEquals(project1.getProjectName(), response.getProjectName());
        assertEquals(project1.getLeader().getUsername(), response.getLeaderName());
        assertEquals(1, response.getMembers().getProjectMembers().size());
        assertEquals(user.getUsername(), response.getMembers().getProjectMembers().get(0).getUsername());

        verify(projectRepository, times(1)).findAll();
    }


    /**
     * <span style="color:red">Negative</span> - Scenario: No Projects Found
     */
    @Test
    @DisplayName("➖ SHOULD_NOT List All Projects Operation (No Projects Found)")
    public void givenNoProjects_WhenListAllProjects_ThenReturnEmptyList() {
        // given (precondition or setup)
        when(projectRepository.findAll())
                .thenReturn(Collections.emptyList());

        // when (action occurs / action or the behaviour that we are going to test)
        List<SearchResponse> searchResponseList = searchProjectService.listAllProjects();

        // then (verify the output)
        assertThat(searchResponseList).isEmpty();
        verify(projectRepository, times(1)).findAll();
    }


    /**
     * Test: List All Projects Containing a String Operation
     */
    @Test
    @DisplayName("➕ List All Projects Containing a String Operation")
    public void givenProjectName_WhenSearchForProject_ThenReturnListOfSimilarProjectsContainingGivenString() {
        // Step 1: Create User and Project Objects (consider moving to a helper method)

        // given (precondition or setup)
        String javaSearchString = "Java";
        when(projectRepository.findByProjectNameContaining(javaSearchString))
                .thenReturn(List.of(project1, project3));
        when(projectMembersRepository.findByProject(project1))
                .thenReturn(List.of(projectMembers1));
        when(projectMembersRepository.findByProject(project3))
                .thenReturn(List.of(projectMembers3));

        // when (action occurs / action or the behaviour that we are going to test)
        List<SearchResponse> searchResponseList = searchProjectService.searchForProject(javaSearchString);

        // then (verify the output)
        SearchResponse searchResponseZero = searchResponseList.get(0);
        SearchResponse searchResponseOne = searchResponseList.get(1);

        assertThat(searchResponseList).hasSize(2);
        assertThat(searchResponseList.get(0).getProjectName())
                .as("Java Project - (Project1)")
                .isEqualTo(project1.getProjectName());
        assertEquals(project1.getProjectName(), searchResponseZero.getProjectName());
        assertEquals(project1.getLeader().getUsername(), searchResponseZero.getLeaderName());
        assertEquals(1, searchResponseZero.getMembers().getProjectMembers().size());
        assertEquals(user.getUsername(), searchResponseZero.getMembers().getProjectMembers().get(0).getUsername());

        assertThat(searchResponseList.get(1).getProjectName())
                .as("Android With Java Project - (Project3)")
                .isEqualTo(project3.getProjectName());
        assertEquals(project3.getProjectName(), searchResponseOne.getProjectName());
        assertEquals(project3.getLeader().getUsername(), searchResponseOne.getLeaderName());
        assertEquals(1, searchResponseOne.getMembers().getProjectMembers().size());
        assertEquals(user.getUsername(), searchResponseOne.getMembers().getProjectMembers().get(0).getUsername());

        verify(projectRepository, times(1)).findByProjectNameContaining(anyString());
    }


    /**
     * <span style="color:red">Negative</span> - Scenario: No Projects Containing the Search String
     */
    @Test
    @DisplayName("➖ SHOULD_NOT List All Projects Containing a String Operation (No Projects Found)")
    public void givenNoProjects_WhenSearchForProject_ThenReturnEmptyList() {
        // given (precondition or setup)
        String nonExistentSearchString = "NonExistentSearchString";
        when(projectRepository.findByProjectNameContaining(nonExistentSearchString))
                .thenReturn(Collections.emptyList());

        // when (action occurs / action or the behaviour that we are going to test)

        // then (verify the output)
        assertThrows(ResourceNotFoundException.class, () -> searchProjectService.searchForProject(nonExistentSearchString));
        verify(projectRepository, times(1)).findByProjectNameContaining(nonExistentSearchString);
    }

}