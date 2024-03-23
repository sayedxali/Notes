package com.dev.vault.util.project;

import com.dev.vault.model.project.Project;
import com.dev.vault.model.project.ProjectMembers;
import com.dev.vault.model.project.UserProjectRole;
import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import com.dev.vault.model.user.enums.Role;
import com.dev.vault.repository.project.ProjectMembersRepository;
import com.dev.vault.repository.project.UserProjectRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for ProjectUtils.
 */
@Service
@RequiredArgsConstructor
@Primary
public class ProjectUtilsImpl implements ProjectUtils {
    private final UserProjectRoleRepository userProjectRoleRepository;
    private final ProjectMembersRepository projectMembersRepository;

    /**
     * Checks if the user is the leader or admin of the project
     *
     * @param project the project to check for leadership or admin role
     * @param user    the user to check for leadership or admin role
     * @return true if the user is the leader or admin of the project, false otherwise
     */
    @Override
    public boolean isLeaderOrAdminOfProject(Project project, User user) {
        // Find the user's role in the project
        Roles leaderOrAdminRole = user.getRoles().stream()
                .filter(roles ->
                        roles.getRole().equals(Role.PROJECT_LEADER) ||
                                roles.getRole().equals(Role.PROJECT_ADMIN)
                ).findFirst()
                .orElse(null);

        // Find the user's role for the specified project
        Optional<UserProjectRole> userProjectRole =
                userProjectRoleRepository.findByUserAndProjectAndRole(user, project, leaderOrAdminRole);

        // Return true if the user has the leader or admin role in the project, false otherwise
        return userProjectRole.isPresent() &&
                (userProjectRole.get().getRole().getRole() == Role.PROJECT_LEADER ||
                        userProjectRole.get().getRole().getRole() == Role.PROJECT_ADMIN);
    }

    /**
     * Checks if the user is a member of the project
     *
     * @param project the project to check for membership
     * @param user    the user to check for membership
     * @return true if the user is a member of the project, false otherwise
     */
    @Override
    public boolean isMemberOfProject(Project project, User user) {
        Optional<ProjectMembers> members = projectMembersRepository.findByProject_ProjectNameAndUser_Email(project.getProjectName(), user.getEmail());
        return members.isPresent();
    }
}
