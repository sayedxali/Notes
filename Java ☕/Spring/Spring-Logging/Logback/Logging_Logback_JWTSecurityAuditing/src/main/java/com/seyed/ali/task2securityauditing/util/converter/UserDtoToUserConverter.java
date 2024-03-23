package com.seyed.ali.task2securityauditing.util.converter;

import com.seyed.ali.task2securityauditing.model.entity.HogwartsUser;
import com.seyed.ali.task2securityauditing.model.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {

    @Override
    public HogwartsUser convert(UserDto source) {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setUsername(source.username());
        hogwartsUser.setEnabled(source.enabled());
        hogwartsUser.setRoles(source.roles());
        return hogwartsUser;
    }

}
