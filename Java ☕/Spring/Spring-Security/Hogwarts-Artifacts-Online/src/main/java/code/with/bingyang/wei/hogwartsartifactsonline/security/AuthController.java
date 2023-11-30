package code.with.bingyang.wei.hogwartsartifactsonline.security;

import code.with.bingyang.wei.hogwartsartifactsonline.system.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.SUCCESS;

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
