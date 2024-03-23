package com.dev.vault.repository.user;

import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import com.dev.vault.model.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * WHAT THE HECK DID I DO???
 */
@DataJpaTest
public class UserRepositoryTest {


    private @Autowired UserRepository userRepository;
    private @MockBean RolesRepository rolesRepository;

    @Test
    public void givenUsersEmail_whenFindByEmail_thenReturnFoundEmail() {
        // Arrange (given) - create a user and save it to the database
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testUsername");
        user.setPassword("testPassword");

        Roles roles = new Roles();
        roles.setRole(Role.TEAM_MEMBER);
        roles.getUsers().add(user);

        when(rolesRepository.findByRole(Role.TEAM_MEMBER))
                .thenReturn(Optional.of(roles));
        userRepository.save(user);

        // Act (when) - retrieve the user by email
        Optional<User> foundUserOptional = userRepository.findByEmail("test@example.com");

        // Assert (then) - verify that the user with the specified email is found
        assertThat(foundUserOptional).isPresent();
        User foundUser = foundUserOptional.get();
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    }


    @Test
    public void givenNonExistentEmail_whenFindByEmail_thenReturnEmptyOptional() {
        // Arrange (given) - no need to explicitly set up data

        // Act (when) - retrieve a user by a non-existent email
        Optional<User> foundUserOptional = userRepository.findByEmail("nonexistent@example.com");

        // Assert (then) - verify that no user with the specified email is found
        assertThat(foundUserOptional).isEmpty();
    }

}