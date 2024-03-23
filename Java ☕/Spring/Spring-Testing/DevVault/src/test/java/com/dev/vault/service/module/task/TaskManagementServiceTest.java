package com.dev.vault.service.module.task;

import com.dev.vault.helper.payload.task.TaskRequest;
import com.dev.vault.helper.payload.task.TaskResponse;
import com.dev.vault.model.project.Project;
import com.dev.vault.model.task.Task;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.task.TaskRepository;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import com.dev.vault.util.project.ProjectUtils;
import com.dev.vault.util.repository.RepositoryUtils;
import com.dev.vault.util.task.TaskUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

import static com.dev.vault.model.task.enums.TaskStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskManagementServiceTest {

    private @InjectMocks TaskManagementServiceImpl taskManagementService;

    //<editor-fold desc="mocks to be injected in `TaskManagementServiceImpl` class.">
    private @Mock TaskRepository taskRepository;
    private @Mock ModelMapper mapper;
    private @Mock AuthenticationService authenticationService;
    private @Mock ProjectUtils projectUtils;
    private @Mock TaskUtils taskUtils;
    private @Mock RepositoryUtils repositoryUtils;
    //</editor-fold>

    //<editor-fold desc="objects to be created before each method">
    private Project mockProject;
    private User mockCurrentUser;
    private TaskRequest mockTaskRequest;
    //</editor-fold>

    @Test
    public void test_ModelMapper() {
        Task expectedTask = new Task();
        expectedTask.setTaskName(mockTaskRequest.getTaskName());
        expectedTask.setTaskStatus(mockTaskRequest.getTaskStatus());
        expectedTask.setDescription(mockTaskRequest.getDescription());
        expectedTask.setCreatedBy(mockCurrentUser);
        expectedTask.setProject(mockProject);
        expectedTask.setTaskStatus(mockTaskRequest.getTaskStatus());

        when(mapper.map(mockTaskRequest, Task.class))
                .thenReturn(expectedTask);
        Task actualMappedTask = mapper.map(mockTaskRequest, Task.class);

        assertThat(actualMappedTask).isEqualTo(expectedTask);
    }

    @BeforeEach
    public void setup() {
        mockCurrentUser = User.builder().userId(1L).username("MockCurrentUser").build();
        mockProject = Project.builder().projectId(1L).projectName("MockProject").leader(mockCurrentUser).build();
        mockTaskRequest = TaskRequest.builder().taskName("MockTaskRequest").taskStatus(IN_PROGRESS).description("Mock Task Request Description").dueDate(LocalDateTime.of(2023, 12, 1, 0, 0)).build();
    }

    @Test // TODO: re-implement this method - getting null value when we hit the method
    @DisplayName("➕ Create New Task Operation")
    public void givenProjectIDAndTaskRequestObj_WhenCreateNewTask_ThenCreateAndReturnTaskResponseObj() {
        // given (precondition or setup)
        Task expectedTask = new Task();
        expectedTask.setTaskName(mockTaskRequest.getTaskName());
        expectedTask.setTaskStatus(mockTaskRequest.getTaskStatus());
        expectedTask.setDescription(mockTaskRequest.getDescription());
        expectedTask.setCreatedBy(mockCurrentUser);
        expectedTask.setProject(mockProject);
        expectedTask.setTaskStatus(mockTaskRequest.getTaskStatus());

        expectedTask.getAssignedUsers().add(mockCurrentUser);

        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject); // negative scenario!
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
//        Check if a task with the same name already exists in the project
        when(taskUtils.doesTaskAlreadyExists(mockTaskRequest, mockProject))
                .thenReturn(false); // negative scenario!
//        Check if the currentUser is a member of the project
        when(projectUtils.isMemberOfProject(mockProject, mockCurrentUser))
                .thenReturn(true); // negative scenario!
//        Check if the currentUser is the leader or admin of the project
        when(projectUtils.isLeaderOrAdminOfProject(mockProject, mockCurrentUser))
                .thenReturn(true); // negative scenario!

        when(mapper.map(mockTaskRequest, Task.class))
                .thenReturn(expectedTask);
        Task actualTask = mapper.map(mockTaskRequest, Task.class);

        when(taskRepository.save(expectedTask))
                .thenReturn(expectedTask);

        // when (action occurs / action or the behaviour that we are going to test)
        TaskResponse createdTestTask = taskManagementService.createNewTask(mockProject.getProjectId(), mockTaskRequest);

        // then (verify the output)
        assertThat(createdTestTask).as("The Task to be created").isNotNull();
        assertThat(createdTestTask.getTaskName()).isEqualTo(expectedTask.getTaskName());
        assertThat(createdTestTask.getProjectName()).isEqualTo(expectedTask.getProject().getProjectName());
        assertThat(createdTestTask.getAssignedUsers()).containsValue(expectedTask.getCreatedBy().getUsername());
    }

}