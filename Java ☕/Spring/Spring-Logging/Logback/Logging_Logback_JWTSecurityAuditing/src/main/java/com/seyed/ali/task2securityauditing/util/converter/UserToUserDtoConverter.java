package com.seyed.ali.task2securityauditing.util.converter;

import com.seyed.ali.task2securityauditing.model.entity.HogwartsUser;
import com.seyed.ali.task2securityauditing.model.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {

    @Override
    public UserDto convert(HogwartsUser source) {
        // We are not setting password in DTO.
        return new UserDto(
                source.getId(),
                source.getUsername(),
                source.isEnabled(),
                source.getRoles()
        );
    }

}
