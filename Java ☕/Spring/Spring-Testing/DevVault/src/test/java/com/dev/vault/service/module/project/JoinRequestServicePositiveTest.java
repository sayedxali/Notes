package com.dev.vault.service.module.project;

import com.dev.vault.helper.payload.group.JoinProjectDTO;
import com.dev.vault.helper.payload.group.JoinResponse;
import com.dev.vault.model.project.JoinCoupon;
import com.dev.vault.model.project.JoinProjectRequest;
import com.dev.vault.model.project.Project;
import com.dev.vault.model.project.enums.JoinStatus;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinRequestServicePositiveTest {

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
    private JoinProjectRequest mockJoinProjectRequest;
    //</editor-fold>

    @BeforeEach
    void setup() {
        mockCurrentUser = User.builder().userId(1L).username("MockCurrentUser").build();
        User mockRequestingUser = User.builder().userId(2L).username("MockRequestingUser").build();

        mockProject = Project.builder().projectId(1L).projectName("MockProject").leader(mockCurrentUser).build();

        mockJoinCoupon = JoinCoupon.builder().couponId(1L).coupon("COUPON").leader(mockCurrentUser).project(mockProject).requestingUser(mockRequestingUser).build();

        mockJoinProjectRequest = JoinProjectRequest.builder().joinRequestId(1L).project(mockProject).user(mockCurrentUser).status(PENDING).build();
    }

    @Test
    @DisplayName("➕ Send Join Request Operation")
    public void givenProjectIDAndStringJoinCoupon_WhenSendJoinRequest_ThenReturnJoinResponseObj() {
        // given (precondition or setup)
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);  // this line of code in the `isCouponValid` is useless; I just noticed 😶‍🌫️😁
        when(repositoryUtils.findUserByEmail_OrElseThrow_ResourceNotFoundException(mockCurrentUser.getEmail()))
                .thenReturn(mockCurrentUser);
//        check if the user already member of the project or has already sent a join project request
        when(joinProjectUtils.isMemberOfProject(mockProject, mockCurrentUser))
                .thenReturn(false); // a negative scenario
//        check if the join request coupon is valid
        when(joinCouponRepository.findByProjectAndRequestingUserAndCoupon(mockProject, mockCurrentUser, mockJoinCoupon.getCoupon()))
                .thenReturn(Optional.of(mockJoinCoupon)); // a negative scenario!
        when(joinCouponRepository.findByCoupon(mockJoinCoupon.getCoupon()))
                .thenReturn(Optional.of(mockJoinCoupon));

        // when (action occurs / action or the behaviour that we are going to test)
        JoinResponse joinResponse = joinRequestService.sendJoinRequest(mockProject.getProjectId(), mockJoinCoupon.getCoupon());

        // then (verify the output)
        assertThat(joinResponse).isNotNull();
        assertThat(joinResponse.getJoinStatus()).isEqualByComparingTo(PENDING);
        assertThat(joinResponse.getStatus()).isEqualTo("Join Project Request Sent successfully. Please wait until ProjectLeader approves your request :)");
    }


    @Test
    @DisplayName("➕ List All JoinProjectRequests for a specified Project Operation")
    public void givenProjectIDAndJoinStatusPENDING_WhenGetJoinRequestsByProjectIdAndStatus_ThenReturnListOfJoinProjectDTO() {
        // given (precondition or setup)
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject); // a negative scenario!
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
//        check if the current user is leader or admin of the project associated with join request
        when(projectUtils.isLeaderOrAdminOfProject(mockProject, mockCurrentUser))
                .thenReturn(true); // a negative scenario!
//        Retrieve the join requests from the repository
        when(joinProjectRequestRepository.findByProject_ProjectIdAndStatus(mockProject.getProjectId(), PENDING))
                .thenReturn(List.of(mockJoinProjectRequest)); // a negative scenario!

        // when (action occurs / action or the behaviour that we are going to test)
        List<JoinProjectDTO> joinProjectRequestDTOs = joinRequestService.getJoinRequestsByProjectIdAndStatus(mockProject.getProjectId(), PENDING);

        // then (verify the output)
        assertThat(joinProjectRequestDTOs).hasSize(1);
        assertThat(joinProjectRequestDTOs.get(0).getJoinRequestId()).isEqualTo(mockJoinProjectRequest.getJoinRequestId());
        assertThat(joinProjectRequestDTOs.get(0).getJoinStatus()).isEqualTo(PENDING);
        assertThat(joinProjectRequestDTOs.get(0).getProjectName()).isEqualTo(mockJoinProjectRequest.getProject().getProjectName());
        assertThat(joinProjectRequestDTOs.get(0).getJoinRequestUsersEmail()).isEqualTo(mockJoinProjectRequest.getUser().getEmail());
    }


    @Test
    void updateJoinRequestStatus() {
    }

}