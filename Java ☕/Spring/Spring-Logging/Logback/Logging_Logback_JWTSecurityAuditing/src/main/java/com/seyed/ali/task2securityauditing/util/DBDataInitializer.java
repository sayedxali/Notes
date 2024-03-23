package com.seyed.ali.task2securityauditing.util;

import com.seyed.ali.task2securityauditing.model.entity.HogwartsUser;
import com.seyed.ali.task2securityauditing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBDataInitializer {

    private final UserService userService;

    @Bean
    public CommandLineRunner run() {
        return _ -> {
            // Create some users.
            HogwartsUser u1 = new HogwartsUser();
            u1.setId(1);
            u1.setUsername("admin");
            u1.setPassword("admin");
            u1.setEnabled(true);
            u1.setRoles("admin user");

            HogwartsUser u2 = new HogwartsUser();
            u2.setId(2);
            u2.setUsername("user");
            u2.setPassword("user");
            u2.setEnabled(true);
            u2.setRoles("user");

            this.userService.save(u1); // because the logic to encode password is int userService.
            this.userService.save(u2); // because the logic to encode password is int userService.
        };
    }

}
