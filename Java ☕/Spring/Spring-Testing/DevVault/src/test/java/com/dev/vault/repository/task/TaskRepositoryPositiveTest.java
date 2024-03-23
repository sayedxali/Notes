package com.dev.vault.repository.task;

import com.dev.vault.model.project.Project;
import com.dev.vault.model.task.Task;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.project.ProjectRepository;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TaskRepositoryPositiveTest {

    private @Autowired TaskRepository taskRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired ProjectRepository projectRepository;

    private Task mockTask;
    private User mockUser1, mockUser2;
    private Project mockProject;

    @BeforeEach
    public void setup() {
        mockUser1 = User.builder()
                .userId(1L)
                .email("test@example.com")
                .build();
        mockUser2 = User.builder()
                .userId(2L)
                .email("test2@example.com")
                .build();

        mockProject = Project.builder()
                .projectId(1L)
                .projectName("testProject")
                .build();

        mockTask = Task.builder()
                .taskId(1L)
                .taskName("testTask")
                .build();

        mockUser1.setTask(List.of(mockTask)); /* rel */
        mockUser2.setTask(List.of(mockTask)); /* rel */

        mockProject.setTasks(List.of(mockTask)); /* rel */
        mockProject.setLeader(mockUser1); /* rel */

        mockTask.setProject(mockProject); /* rel */
        mockTask.setAssignedUsers(Set.of(mockUser1, mockUser2)); /* rel */
    }

    @Test
    @DisplayName("➕ Find Task By AssignedUsers And TaskID ( + ) Operation")
    public void givenAssignedUsersAndTaskID_WhenFindByAssignedUsersAndTaskID_ThenReturnOptionalOfTask() {
        // given (precondition or setup)
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        taskRepository.save(mockTask);
        projectRepository.save(mockProject);

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Task> foundTask1 = taskRepository.findByAssignedUsersAndTaskId(mockUser1, mockTask.getTaskId());
        Optional<Task> foundTask2 = taskRepository.findByAssignedUsersAndTaskId(mockUser2, mockTask.getTaskId());

        // then (verify the output)
        if (foundTask1.isPresent()) {
            assertThat(foundTask1).isPresent();
            assertThat(foundTask1.get().getTaskId()).isEqualTo(1L);
            assertThat(foundTask1.get().getTaskName()).isEqualTo("testTask");
        }

        if (foundTask2.isPresent()) {
            assertThat(foundTask2).isPresent();
            assertThat(foundTask2.get().getTaskId()).isEqualTo(1L);
            assertThat(foundTask2.get().getTaskName()).isEqualTo("testTask");
        }
    }


    @Test
    @DisplayName("➕ Find Task By Project And TaskName ( + ) Operation")
    public void givenProjectObjAndTaskName_WhenFindByProjectAndTaskName_ThenReturnOptionalOfTask() {
        // given (precondition or setup)
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        Project savedProject = projectRepository.save(mockProject);
        taskRepository.save(mockTask);

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Task> foundTask = taskRepository.findByProjectAndTaskName(mockProject, mockTask.getTaskName());

        // then (verify the output)
        if (foundTask.isPresent()) {
            assertThat(foundTask).isPresent();
            assertThat(foundTask.get().getTaskId()).isEqualTo(1L);
            assertThat(foundTask.get().getTaskName()).isEqualTo(mockTask.getTaskName());
            assertThat(foundTask.get().getProject()).isEqualTo(savedProject);
        }
    }

}