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
public class TaskRepositoryNegativeTest {


    private @Autowired TaskRepository taskRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired ProjectRepository projectRepository;

    private Task mockTask;
    private User mockUser1, mockUser2;
    private Project mockProject;

    @BeforeEach
    public void setup() {
        mockUser1 = User.builder().userId(1L).email("test@example.com").build();
        mockUser2 = User.builder().userId(2L).email("test2@example.com").build();

        mockProject = Project.builder().projectId(1L).projectName("testProject").build();

        mockTask = Task.builder().taskId(1L).taskName("testTask").build();

        mockUser1.setTask(List.of(mockTask)); /* rel */
        mockUser2.setTask(List.of(mockTask)); /* rel */

        mockProject.setTasks(List.of(mockTask)); /* rel */
        mockProject.setLeader(mockUser1); /* rel */

        mockTask.setProject(mockProject); /* rel */
        mockTask.setAssignedUsers(Set.of(mockUser1, mockUser2)); /* rel */
    }


    @Test
    @DisplayName("➖ DON'T Find Task By INVALID AssignedUsers VALID And TaskID ( + ) Operation")
    public void givenInvalidAssignedUsersAndValidTaskID_WhenFindByAssignedUsersAndTaskID_ThenReturnOptionalOfEmpty() {
        // given (precondition or setup)
        User invalidAssignedUsers = new User();
        invalidAssignedUsers.setUserId(111L);
        invalidAssignedUsers.setEmail("invalid@example.com");

        userRepository.save(invalidAssignedUsers);
        taskRepository.save(mockTask);
        projectRepository.save(mockProject);

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Task> foundTask = taskRepository.findByAssignedUsersAndTaskId(invalidAssignedUsers, mockTask.getTaskId());

        // then (verify the output)
        assertThat(foundTask).isEmpty();
    }


    @Test
    @DisplayName("➖ DON'T Find Task By VALID AssignedUsers INVALID And TaskID ( + ) Operation")
    public void givenValidAssignedUsersAndInvalidTaskID_WhenFindByAssignedUsersAndTaskID_ThenReturnOptionalOfEmpty() {
        // given (precondition or setup)
        long invalidTaskID = 0L;
        userRepository.save(mockUser1);
        taskRepository.save(mockTask);
        projectRepository.save(mockProject);

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Task> foundTask = taskRepository.findByAssignedUsersAndTaskId(mockUser1, invalidTaskID);

        // then (verify the output)
        assertThat(foundTask).isEmpty();
    }


    @Test
    @DisplayName("➖ DON'T Find Task By INVALID Project And VALID TaskName ( + ) Operation")
    public void givenInvalidProjectObjAndTaskName_WhenFindByProjectAndTaskName_ThenReturnOptionalOfEmpty() {
        // given (precondition or setup)
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);

        Project invalidProject = new Project();
        Project savedProject = projectRepository.save(invalidProject);
        taskRepository.save(mockTask);

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Task> foundTask = taskRepository.findByProjectAndTaskName(invalidProject, mockTask.getTaskName());

        // then (verify the output)
        assertThat(foundTask).isEmpty();
    }


    @Test
    @DisplayName("➖ DON'T Find Task By VALID Project And INVALID TaskName ( + ) Operation")
    public void givenValidProjectObjAndInvalidTaskName_WhenFindByProjectAndTaskName_ThenReturnOptionalOfEmpty() {
        // given (precondition or setup)
        String invalidTaskName = "invalidTaskName";
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        projectRepository.save(mockProject);
        taskRepository.save(mockTask);

        // when (action occurs / action or the behaviour that we are going to test)
        Optional<Task> foundTask = taskRepository.findByProjectAndTaskName(mockProject, invalidTaskName);

        // then (verify the output)
        assertThat(foundTask).isEmpty();
    }

}
