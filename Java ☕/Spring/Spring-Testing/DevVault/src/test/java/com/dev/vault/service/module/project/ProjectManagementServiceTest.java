package com.dev.vault.service.module.project;

import com.dev.vault.helper.exception.ResourceAlreadyExistsException;
import com.dev.vault.helper.payload.group.ProjectDto;
import com.dev.vault.model.project.Project;
import com.dev.vault.model.project.ProjectMembers;
import com.dev.vault.model.project.UserProjectRole;
import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.project.ProjectMembersRepository;
import com.dev.vault.repository.project.ProjectRepository;
import com.dev.vault.repository.project.UserProjectRoleRepository;
import com.dev.vault.repository.user.UserRepository;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import com.dev.vault.util.repository.RepositoryUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.dev.vault.model.user.enums.Role.PROJECT_LEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests for ProjectManagement :
 * <ul>
 *     <li><span style="color:yellow">CreateProject</span> : Positive & Negative Scenario</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class ProjectManagementServiceTest {

    private @InjectMocks ProjectManagementServiceImpl projectManagementService;

    //<editor-fold desc="mocks of `ProjectManagementServiceImpl` class">
    private @Mock UserRepository userRepository;
    private @Mock UserProjectRoleRepository userProjectRoleRepository;
    private @Mock ProjectMembersRepository projectMembersRepository;
    private @Mock ProjectRepository projectRepository;
    private @Mock ModelMapper modelMapper;
    private @Mock AuthenticationService authenticationService;
    private @Mock RepositoryUtils repositoryUtils;
    //</editor-fold>

    //<editor-fold desc="Objects to be created before each test">
    private ProjectDto projectDto;
    private Project project;
    private Roles projectLeaderRole;
    private User currentUser;

    //</editor-fold>

    @BeforeEach
    public void setup() {
        // Create a ProjectDto object and set its name
        projectDto = new ProjectDto();
        projectDto.setProjectName("Test Project");

        // Create a Project object and set its name
        project = new Project();
        project.setProjectName(projectDto.getProjectName());

        // Create a Roles object and set its role
        projectLeaderRole = new Roles();
        projectLeaderRole.setRole(PROJECT_LEADER);

        // Create a User object and set its roles
        Set<Roles> rolesSet = new HashSet<>();
        rolesSet.add(projectLeaderRole);
        currentUser = new User();
        currentUser.setRoles(rolesSet);
    }

    /**
     * Test: Create Project Operation
     */
    @Test
    @DisplayName("➕ Create Project Operation")
    public void givenProjectDTOObj_WhenCreateProject_ThenReturnProjectDTOObj() {
        // Step 1: Create Project and Roles Objects (consider moving to a helper method)

        // given (precondition or setup)
        when(projectRepository.findByProjectNameAllIgnoreCase(projectDto.getProjectName()))
                .thenReturn(Optional.empty());
        when(repositoryUtils.findRoleByRole_OrElseThrow_ResourceNotFoundException(PROJECT_LEADER))
                .thenReturn(projectLeaderRole);
        when(authenticationService.getCurrentUser())
                .thenReturn(currentUser);
        when(modelMapper.map(projectDto, Project.class))
                .thenReturn(project);

        // when (action occurs / action or the behaviour that we are going to test)
        // Call the method under test and store the result
        ProjectDto result = projectManagementService.createProject(projectDto);

        // then (verify the output)
        assertEquals(projectDto.getProjectName(), result.getProjectName());
        verify(userRepository, times(1)).save(currentUser);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMembersRepository, times(1)).save(any(ProjectMembers.class));
        verify(userProjectRoleRepository, times(1)).save(any(UserProjectRole.class));
        verify(projectMembersRepository, times(1)).save(any(ProjectMembers.class));
        verify(userProjectRoleRepository, times(1)).save(any(UserProjectRole.class));
    }


    /**
     * Test: SHOULD NOT Create Project (given existing `projectName`)
     */
    @Test
    @DisplayName("➖ SHOULD NOT Create Project (given existing `projectName`) Operation")
    public void givenExistingProjectDTOObj_WhenCreateProject_ThenReturnProjectDTOObj() {
        // Step 1: Create Project Object (consider moving to a helper method)

        // given (precondition or setup)
        Project existingProject = new Project();
        existingProject.setProjectName(projectDto.getProjectName());

        when(projectRepository.findByProjectNameAllIgnoreCase(projectDto.getProjectName()))
                .thenReturn(Optional.of(existingProject));

        // when (action occurs / action or the behaviour that we are going to test)
        // Call the method under test and store the result: act and assert is being called
        // in the then section altogether

        // then (verify the output)
        assertThrows(ResourceAlreadyExistsException.class, () -> projectManagementService.createProject(projectDto));
    }

}
