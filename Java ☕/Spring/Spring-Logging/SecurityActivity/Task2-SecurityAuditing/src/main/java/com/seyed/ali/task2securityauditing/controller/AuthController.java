package com.seyed.ali.task2securityauditing.controller;


import com.seyed.ali.task2securityauditing.service.AuthService;
import com.seyed.ali.task2securityauditing.model.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.seyed.ali.task2securityauditing.model.dto.StatusCode.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/users")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result getLoginInfo(Authentication authentication) {
        log.debug("Authenticated user: '{}'", authentication.getName());
        return Result.builder()
                .flag(true)
                .code(SUCCESS)
                .message("User Info and JSON Web Token")
                .data(this.authService.createLoginInfo(authentication))
                .build();
    }

}
