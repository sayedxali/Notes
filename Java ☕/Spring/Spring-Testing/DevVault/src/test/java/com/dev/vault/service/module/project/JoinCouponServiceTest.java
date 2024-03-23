package com.dev.vault.service.module.project;

import com.dev.vault.helper.exception.NotLeaderOfProjectException;
import com.dev.vault.helper.exception.ResourceAlreadyExistsException;
import com.dev.vault.helper.exception.ResourceNotFoundException;
import com.dev.vault.model.project.JoinCoupon;
import com.dev.vault.model.project.Project;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.project.JoinCouponRepository;
import com.dev.vault.repository.project.ProjectRepository;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import com.dev.vault.util.project.ProjectUtils;
import com.dev.vault.util.repository.RepositoryUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinCouponServiceTest {

    private @InjectMocks JoinCouponServiceImpl joinCouponService;

    //<editor-fold desc="mocks to be injected in `JoinCouponServiceImpl` class">
    private @Mock JoinCouponRepository joinCouponRepository;
    private @Mock ProjectRepository projectRepository;
    private @Mock AuthenticationService authenticationService;
    private @Mock ProjectUtils projectUtils;
    private @Mock RepositoryUtils repositoryUtils;
    //</editor-fold>

    //<editor-fold desc="objects to be created before each method">
    private User mockCurrentUser, mockRequestingUser;
    private Project mockProject;
    private JoinCoupon mockJoinCoupon;
    //</editor-fold>

    @BeforeEach
    public void setup() {
        mockCurrentUser = User.builder().userId(1L).username("MockCurrentUser").build();
        mockRequestingUser = User.builder().userId(2L).username("MockRequestingUser").build();

        mockProject = Project.builder().projectId(1L).leader(mockCurrentUser).projectName("MockProject").memberCount(1).build();

        mockJoinCoupon = JoinCoupon.builder().couponId(1L).coupon("MOCK_COUPON_CODE").build();
    }

    /**
     * Test: Generate One Time Join Coupon for Requesting User In a Project
     */
    @Test
    @DisplayName("➕ Generate One Time Join Coupon Operation (user IS leader of project)")
    public void givenProjectIDAndRequestingUserID_WhenGenerateOneTimeJoinCoupon_ThenReturnJoinCouponString() {
        // given (precondition or setup)
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(repositoryUtils.findUserById_OrElseThrow_ResourceNotFoundException(mockRequestingUser.getUserId()))
                .thenReturn(mockRequestingUser);
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(projectUtils.isLeaderOrAdminOfProject(mockProject, mockCurrentUser))
                .thenReturn(true);
        when(joinCouponRepository.findByRequestingUserAndProject(mockRequestingUser, mockProject))
                .thenReturn(Optional.empty());
        when(projectRepository.findByProjectName(mockProject.getProjectName()))
                .thenReturn(Optional.of(mockProject));

        // when (action occurs / action or the behaviour that we are going to test)
        String generatedOneTimeJoinCoupon = joinCouponService.generateOneTimeJoinCoupon(mockProject.getProjectId(), mockRequestingUser.getUserId());

        // then (verify the output)
        assertThat(generatedOneTimeJoinCoupon).isNotNull();

        verify(repositoryUtils, times(1)).findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId());
        verify(repositoryUtils, times(1)).findUserById_OrElseThrow_ResourceNotFoundException(mockRequestingUser.getUserId());
        verify(authenticationService, times(1)).getCurrentUser();
        verify(projectUtils, times(1)).isLeaderOrAdminOfProject(mockProject, mockCurrentUser);
        verify(joinCouponRepository, times(1)).findByRequestingUserAndProject(mockRequestingUser, mockProject);
        verify(projectRepository, times(1)).findByProjectName(mockProject.getProjectName());
    }


    /**
     * <span style="color:red">Negative</span> - Scenario:  When the project does not exist
     */
    @Test
    @DisplayName("➖ SHOULD_NOT Generate One Time Join Coupon Operation (`project` DOES_NOT exist)")
    public void givenInvalidProjectIDAndRequestingUserID_WhenGenerateOneTimeJoinCoupon_ThenThrowsResourceNotFoundException() {
        // given (precondition or setup)
        long mockInvalidProjectID = -1L;
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockInvalidProjectID))
                .thenThrow(new ResourceNotFoundException("Project", "ProjectID", String.valueOf(mockInvalidProjectID))); // invalid mockProjectID

        // when (action occurs / action or the behaviour that we are going to test)
        ResourceNotFoundException assertedThrows = assertThrows(
                ResourceNotFoundException.class,
                () -> joinCouponService.generateOneTimeJoinCoupon(mockInvalidProjectID, anyLong())
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage()).isNotNull();

        verify(repositoryUtils, times(1)).findProjectById_OrElseThrow_ResourceNotFoundException(mockInvalidProjectID);
    }


    /**
     * <span style="color:red">Negative</span> - Scenario: When the requestingUser does not exist
     */
    @Test
    @DisplayName("➖ SHOULD_NOT Generate One Time Join Coupon Operation (`requestingUser` DOES_NOT exist)")
    public void givenProjectIDAndInvalidRequestingUserID_WhenGenerateOneTimeJoinCoupon_ThenThrowResourceNotFoundException() {
        // given (precondition or setup)
        long mockInvalidRequestingUserID = -1L;
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(repositoryUtils.findUserById_OrElseThrow_ResourceNotFoundException(mockInvalidRequestingUserID))
                .thenThrow(new ResourceNotFoundException("User", "UserID", String.valueOf(mockInvalidRequestingUserID))); // requestingUser is invalid

        // when (action occurs / action or the behaviour that we are going to test)
        ResourceNotFoundException assertedThrows = assertThrows(
                ResourceNotFoundException.class,
                () -> joinCouponService.generateOneTimeJoinCoupon(mockProject.getProjectId(), mockInvalidRequestingUserID)
        );

        // then (verify the output)
        assertThat(assertedThrows).isNotNull();

        verify(repositoryUtils, times(1)).findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId());
        verify(repositoryUtils, times(1)).findUserById_OrElseThrow_ResourceNotFoundException(mockInvalidRequestingUserID);
    }


    /**
     * <span style="color:red">Negative</span> - Scenario: When the current user is not the leader or admin of the project
     */
    @Test
    @DisplayName("➖ SHOULD_NOT Generate One Time Join Coupon Operation (user IS_NOT leader of project)")
    public void givenProjectIDAndRequestingUserID_WhenGenerateOneTimeJoinCoupon_ThenThrowNotLeaderOfProjectException() {
        // given (precondition or setup)
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(repositoryUtils.findUserById_OrElseThrow_ResourceNotFoundException(mockRequestingUser.getUserId()))
                .thenReturn(mockRequestingUser);
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(projectUtils.isLeaderOrAdminOfProject(mockProject, mockCurrentUser))
                .thenReturn(false); // User is not a leader

        // when (action occurs / action or the behaviour that we are going to test)
        Throwable exception = assertThrows(
                NotLeaderOfProjectException.class,
                () -> joinCouponService.generateOneTimeJoinCoupon(mockProject.getProjectId(), mockRequestingUser.getUserId())
        );

        // then (verify the output)
        assertEquals("❌ You are not the leader or admin of this project ❌", exception.getMessage());

        verify(repositoryUtils, times(1)).findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId());
        verify(repositoryUtils, times(1)).findUserById_OrElseThrow_ResourceNotFoundException(mockRequestingUser.getUserId());
        verify(authenticationService, times(1)).getCurrentUser();
        verify(projectUtils, times(1)).isLeaderOrAdminOfProject(mockProject, mockCurrentUser);
    }


    /**
     * <span style="color:red">Negative</span> - Scenario:  When a join coupon has already been generated for the requesting user and project
     */
    @Test
    @DisplayName("➖ SHOULD_NOT Generate One Time Join Coupon Operation (`joinCoupon` ALREADY_EXISTS/GENERATED for the `requestingUser`)")
    public void givenProjectIDAndAlreadyGeneratedCouponRequestingUserID_WhenGenerateOneTimeJoinCoupon_ThenThrowsResourceAlreadyExistsException() {
        // given (precondition or setup)
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(repositoryUtils.findUserById_OrElseThrow_ResourceNotFoundException(mockRequestingUser.getUserId()))
                .thenReturn(mockRequestingUser);
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(projectUtils.isLeaderOrAdminOfProject(mockProject, mockCurrentUser))
                .thenReturn(true);
        when(joinCouponRepository.findByRequestingUserAndProject(mockRequestingUser, mockProject))
                .thenReturn(Optional.of(mockJoinCoupon)); // mockJoinCoupon is already generated

        // when (action occurs / action or the behaviour that we are going to test)
        ResourceAlreadyExistsException assertedThrows = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> joinCouponService.generateOneTimeJoinCoupon(mockProject.getProjectId(), mockRequestingUser.getUserId())
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage()).isEqualTo("A coupon is already generated for: " + mockRequestingUser.getUsername());

        verify(repositoryUtils, times(1)).findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId());
        verify(repositoryUtils, times(1)).findUserById_OrElseThrow_ResourceNotFoundException(mockRequestingUser.getUserId());
        verify(authenticationService, times(1)).getCurrentUser();
        verify(projectUtils, times(1)).isLeaderOrAdminOfProject(mockProject, mockCurrentUser);
        verify(joinCouponRepository, times(1)).findByRequestingUserAndProject(mockRequestingUser, mockProject);
    }

}