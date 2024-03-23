package com.dev.vault.repository;

import com.dev.vault.helper.payload.auth.RegisterRequest;
import com.dev.vault.model.user.Roles;
import com.dev.vault.model.user.User;
import com.dev.vault.repository.user.RolesRepository;
import com.dev.vault.repository.user.UserRepository;
import com.dev.vault.service.interfaces.user.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.dev.vault.model.user.enums.Role.TEAM_MEMBER;

@Component
@RequiredArgsConstructor
public class DBDataInitializer {

    private final AuthenticationService authenticationService;
    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Roles roles = new Roles();
            roles.setRole(TEAM_MEMBER);

            this.rolesRepository.save(roles);

            User user = new User();
            user.setUserId(1L);
            user.setUsername("seyed");
            user.setEmail("seyed@gmail.com");
            user.setPassword("pass");
            user.setActive(true);

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(user.getUsername());
            registerRequest.setEmail(user.getEmail());
            registerRequest.setPassword(user.getPassword());
            registerRequest.setUsername(user.getUsername());

            this.authenticationService.registerUser(registerRequest);
            System.out.println(this.userRepository.findByEmail("seyed@gmail.com").get().getPassword());
        };
    }

}
