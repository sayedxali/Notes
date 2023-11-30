package code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser.converter;

import code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser.HogwartsUser;
import code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
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
