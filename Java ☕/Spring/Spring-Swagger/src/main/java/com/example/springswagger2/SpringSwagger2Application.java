package com.example.springswagger2;

import com.example.springswagger2.config.jwt.JWTService;
import com.example.springswagger2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SpringSwagger2Application {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringSwagger2Application.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
//            String token = jwtService.generateToken(userRepository.findByUsername("admin").get());
//            log.info("The token :: {}", token);
        };
    }

}
