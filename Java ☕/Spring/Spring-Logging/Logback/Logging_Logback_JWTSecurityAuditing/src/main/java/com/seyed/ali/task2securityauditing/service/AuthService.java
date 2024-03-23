package com.seyed.ali.task2securityauditing.service;

import com.seyed.ali.task2securityauditing.config.security.jwt.JwtProvider;
import com.seyed.ali.task2securityauditing.model.entity.HogwartsUser;
import com.seyed.ali.task2securityauditing.model.entity.MyUserPrincipal;
import com.seyed.ali.task2securityauditing.util.converter.UserToUserDtoConverter;
import com.seyed.ali.task2securityauditing.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create user info.
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);

        // Create a JWT.
        String token = this.jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);
        return loginResultMap;
    }

}
