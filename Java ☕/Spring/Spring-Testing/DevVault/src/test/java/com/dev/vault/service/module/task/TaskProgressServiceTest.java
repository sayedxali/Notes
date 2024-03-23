package com.dev.vault.service.module.task;

import com.dev.vault.model.project.Project;
import com.dev.vault.model.task.Task;
import com.dev.vault.model.task.enums.TaskStatus;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.task.TaskRepository;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import com.dev.vault.util.repository.RepositoryUtils;
import com.dev.vault.util.task.TaskUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.dev.vault.model.task.enums.TaskStatus.COMPLETED;
import static com.dev.vault.model.task.enums.TaskStatus.IN_PROGRESS;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskProgressServiceTest {

    private @InjectMocks TaskProgressServiceImpl taskProgressService;

    //<editor-fold desc="mocks to be injected in `TaskProgressServiceImpl` class">
    private @Mock TaskRepository taskRepository;
    private @Mock TaskUtils taskUtils;
    private @Mock RepositoryUtils repositoryUtils;
    private @Mock AuthenticationService authenticationService;
    //</editor-fold>

    //<editor-fold desc="objects to be created before each test">
    private Task mockTask;
    private Project mockProject;
    private User mockCurrentUser;
    private TaskStatus mockTaskStatus;
    //</editor-fold>

    @BeforeEach
    public void setup() {
        mockCurrentUser = User.builder().userId(1L).username("MockCurrentUser").build();
        mockTaskStatus = COMPLETED;

        mockTask = Task.builder().taskId(1L).taskName("MockTask").project(mockProject).assignedUsers(Set.of(mockCurrentUser)).build();

        mockProject = Project.builder().projectId(1L).projectName("MockProject").build();
        mockProject.setLeader(mockCurrentUser);
        mockProject.setTasks(List.of(mockTask));
    }

    @Test
    @DisplayName("➕ Mark Task As Completed Operation")
    public void givenTaskIDAndProjectIDAndTaskStatus_WhenMarkTaskAsCompleted_ThenReturnNothing() {
        // given (precondition or setup)
        when(repositoryUtils.findTaskById_OrElseThrow_ResourceNotFoundException(mockTask.getTaskId()))
                .thenReturn(mockTask);
        when(repositoryUtils.findProjectById_OrElseThrow_ResourceNotFoundException(mockProject.getProjectId()))
                .thenReturn(mockProject);
        when(authenticationService.getCurrentUser())
                .thenReturn(mockCurrentUser);
//        Validate that the task belongs to the project and the user is a member or leader/admin of the project
        doNothing().when(taskUtils).validateTaskAndProject(mockTask, mockProject, mockCurrentUser);

        mockTask.setTaskStatus(IN_PROGRESS); // negative scenario!
        mockTask.setDueDate(LocalDateTime.of(2023, 12, 1, 0, 0)); // negative scenario!
        when(taskRepository.save(mockTask))
                .thenReturn(mockTask);

        // when (action occurs / action or the behaviour that we are going to test)
        taskProgressService.markTaskAsCompleted(mockTask.getTaskId(), mockProject.getProjectId(), COMPLETED);

        // then (verify the output)
        verify(taskUtils, times(1)).validateTaskAndProject(mockTask, mockProject, mockCurrentUser);
        verify(taskRepository, times(1)).save(mockTask);
    }

}