package com.dev.vault.service.module.project;

import com.dev.vault.helper.exception.DevVaultException;
import com.dev.vault.helper.exception.NotLeaderOfProjectException;
import com.dev.vault.helper.exception.ResourceAlreadyExistsException;
import com.dev.vault.helper.exception.ResourceNotFoundException;
import com.dev.vault.helper.payload.group.JoinProjectDTO;
import com.dev.vault.model.project.JoinCoupon;
import com.dev.vault.model.project.Project;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.project.JoinCouponRepository;
import com.dev.vault.repository.project.JoinProjectRequestRepository;
import com.dev.vault.repository.project.ProjectMembersRepository;
import com.dev.vault.repository.project.ProjectRepository;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import com.dev.vault.util.project.JoinRequestProjectUtilsImpl;
import com.dev.vault.util.project.ProjectUtilsImpl;
import com.dev.vault.util.repository.RepositoryUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.dev.vault.model.project.enums.JoinStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinRequestServiceNegativeTest {

    private @InjectMocks JoinRequestServiceImpl joinRequestService;

    //<editor-fold desc="mocks to be injected in `JoinRequestServiceImpl` class">
    private @Mock JoinCouponRepository joinCouponRepository;
    private @Mock ProjectMembersRepository projectMembersRepository;
    private @Mock ProjectRepository projectRepository;
    private @Mock JoinProjectRequestRepository joinProjectRequestRepository;
    private @Mock AuthenticationService authenticationService;
    private @Mock RepositoryUtils repositoryUtils;
    private @Mock
    @Qualifier("projectUtilsImpl") ProjectUtilsImpl projectUtils;
    //    private @Mock @Qualifier("projectUtilsImpl") ProjectUtils projectUtils;
    private @Mock
    @Qualifier("joinRequestProjectUtilsImpl") JoinRequestProjectUtilsImpl joinProjectUtils;
    //</editor-fold>

    //<editor-fold desc="objects to be created before each test">
    private User mockCurrentUser;
    private Project mockProject;
    private JoinCoupon mockJoinCoupon;
    //</editor-fold>

    @BeforeEach
    void setup() {
        mockCurrentUser = User.builder().userId(1L).username("MockCurrentUser").build();
        User mockRequestingUser = User.builder().userId(2L).username("MockRequestingUser").build();

        mockProject = Project.builder().projectId(1L).projectName("MockProject").leader(mockCurrentUser).build();

        mockJoinCoupon = JoinCoupon.builder().couponId(1L).coupon("COUPON").leader(mockCurrentUser).project(mockProject).requestingUser(mockRequestingUser).build();
    }

    @Test
    @DisplayName("➖ SHOULD_NOT Send Join Request Operation (`project` DOES_NOT exist)")
    public void givenInvalidProjectIDAndStringJoinCoupon_WhenSendJoinRequest_ThenThrowResourceNotFoundException() {
        // given (precondition or setup)
        long mockInvalidProjectID = -1;
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockInvalidProjectID))
                .thenThrow(new ResourceNotFoundException("Project", "ProjectID", String.valueOf(mockInvalidProjectID)));

        // when (action occurs / action or the behaviour that we are going to test)
        ResourceNotFoundException assertedThrows = assertThrows(
                ResourceNotFoundException.class,
                () -> joinRequestService.sendJoinRequest(mockInvalidProjectID, mockJoinCoupon.getCoupon())
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage()).isEqualTo("Project not found with ProjectID: " + mockInvalidProjectID);
    }


    @Test
    @DisplayName("➖ SHOULD_NOT Send Join Request Operation (user ALREADY_MEMBER of project)")
    public void givenProjectIDAndAlreadyMemberStringJoinCoupon_WhenSendJoinRequest_ThenThrowResourceAlreadyExistsException() {
        // given (precondition or setup)
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(repositoryUtils.findUserByEmail_OrElseThrow_ResourceNotFoundException(mockCurrentUser.getEmail()))
                .thenReturn(mockCurrentUser);
        when(joinProjectUtils.isMemberOfProject(mockProject, mockCurrentUser))
                .thenReturn(true); // already member of project

        // when (action occurs / action or the behaviour that we are going to test)
        ResourceAlreadyExistsException assertedThrows = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> joinRequestService.sendJoinRequest(mockProject.getProjectId(), mockJoinCoupon.getCoupon())
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage()).isEqualTo("The Resource JoinRequest, with Member: '" + mockCurrentUser.getEmail() + "' already exists!");
    }


    @Test
    @DisplayName("➖ SHOULD_NOT Send Join Request Operation (coupon NOT_VALID - ALREADY_EXISTS)")
    public void givenProjectIDAndInvalidStringJoinCoupon_WhenSendJoinRequest_ThenThrowsDevVaultException() {
        // given (precondition or setup)
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(repositoryUtils.findUserByEmail_OrElseThrow_ResourceNotFoundException(mockCurrentUser.getEmail()))
                .thenReturn(mockCurrentUser);
        when(joinProjectUtils.isMemberOfProject(mockProject, mockCurrentUser))
                .thenReturn(false);
        when(joinCouponRepository.findByProjectAndRequestingUserAndCoupon(mockProject, mockCurrentUser, mockJoinCoupon.getCoupon()))
                .thenReturn(Optional.empty()); // coupon not valid!

        // when (action occurs / action or the behaviour that we are going to test)
        DevVaultException assertedThrows = assertThrows(
                DevVaultException.class,
                () -> joinRequestService.sendJoinRequest(mockProject.getProjectId(), mockJoinCoupon.getCoupon())
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage())
                .isEqualTo(
                        "This JoinRequestCoupon is either; " +
                                "1. Not for this project: {" + mockProject.getProjectName() + "}" +
                                " | 2. Not for this user: {" + mockCurrentUser.getUsername() + "}"
                );
    }


    @Test
    @DisplayName("➖ SHOULD_NOT Send Join Request Operation (coupon NOT_VALID - ALREADY_USED)")
    public void givenProjectIDAndAlreadyUsedStringJoinCoupon_WhenSendJoinRequest_ThenThrowsDevVaultException() {
        // given (precondition or setup)
        JoinCoupon mockJoinCouponForThisMethod = mock(JoinCoupon.class);
        mockJoinCouponForThisMethod.setCoupon("RANDOM_COUPON");

        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(repositoryUtils.findUserByEmail_OrElseThrow_ResourceNotFoundException(mockCurrentUser.getEmail()))
                .thenReturn(mockCurrentUser);
        when(joinProjectUtils.isMemberOfProject(mockProject, mockCurrentUser))
                .thenReturn(false);
        when(joinCouponRepository.findByProjectAndRequestingUserAndCoupon(mockProject, mockCurrentUser, mockJoinCouponForThisMethod.getCoupon()))
                .thenReturn(Optional.of(mockJoinCouponForThisMethod));
        when(mockJoinCouponForThisMethod.isUsed())
                .thenReturn(true); // coupon already used!

        // when (action occurs / action or the behaviour that we are going to test)
        DevVaultException assertedThrows = assertThrows(
                DevVaultException.class,
                () -> joinRequestService.sendJoinRequest(mockProject.getProjectId(), mockJoinCouponForThisMethod.getCoupon())
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage()).isEqualTo("You have already used this coupon. Please request for another one.");
    }


    @Test
    @DisplayName("➖ SHOULD_NOT List All JoinProjectRequests for a specified Project Operation (`project` DOES_NOT exist)")
    public void givenInvalidProjectIDAndJoinStatusPENDING_WhenGetJoinRequestsByProjectIdAndStatus_ThenThrowResourceNotFoundException() {
        // given (precondition or setup)
        long mockInvalidProjectId = -1;
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockInvalidProjectId))
                .thenThrow(new ResourceNotFoundException("Project", "ProjectID", String.valueOf(mockInvalidProjectId))); // project not valid!

        // when (action occurs / action or the behaviour that we are going to test)
        ResourceNotFoundException assertedThrows = assertThrows(
                ResourceNotFoundException.class,
                () -> joinRequestService.getJoinRequestsByProjectIdAndStatus(mockInvalidProjectId, PENDING)
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage()).isEqualTo("Project not found with ProjectID: " + mockInvalidProjectId);
    }


    @Test
    @DisplayName("➖ SHOULD_NOT List All JoinProjectRequests for a specified Project Operation (`currentUser` IS_NOT leader of `project`)")
    public void givenProjectIDAndJoinStatusPENDINGAndNonLeaderCurrentUser_WhenGetJoinRequestsByProjectIdAndStatus_ThenThrowNotLeaderOfProjectException() {
        // given (precondition or setup)
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(projectUtils.isLeaderOrAdminOfProject(mockProject, mockCurrentUser))
                .thenReturn(false); // currentUser is not leader!

        // when (action occurs / action or the behaviour that we are going to test)
        NotLeaderOfProjectException assertedThrows = assertThrows(
                NotLeaderOfProjectException.class,
                () -> joinRequestService.getJoinRequestsByProjectIdAndStatus(mockProject.getProjectId(), PENDING)
        );

        // then (verify the output)
        assertThat(assertedThrows.getMessage()).isEqualTo("👮🏻 you are not the leader or admin of this project 👮🏻");
    }


    @Test
    @DisplayName("➖ SHOULD_NOT List All JoinProjectRequests for a specified Project Operation (`joinProjectRequest` IS_EMPTY)")
    public void givenProjectIDAndJoinStatusPENDING_WhenGetJoinRequestsByProjectIdAndStatus_ThenReturnListOfJoinProjectDTO() {
        // given (precondition or setup)
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(projectUtils.isLeaderOrAdminOfProject(mockProject, mockCurrentUser))
                .thenReturn(true);
        when(joinProjectRequestRepository.findByProject_ProjectIdAndStatus(mockProject.getProjectId(), PENDING))
                .thenReturn(Collections.emptyList()); // empty joinProjectRequest!

        // when (action occurs / action or the behaviour that we are going to test)
        List<JoinProjectDTO> joinProjectRequestDTOs = joinRequestService.getJoinRequestsByProjectIdAndStatus(mockProject.getProjectId(), PENDING);

        // then (verify the output)
        assertThat(joinProjectRequestDTOs).isEmpty();
    }

}