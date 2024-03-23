package com.seyed.ali.task2securityauditing.model.dto;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

/**
 * DTO for {@link code.with.bingyang.wei.hogwartsartifactsonline.hogwartsuser.HogwartsUser}
 */
public record UserDto(Integer id,
                      @NotEmpty(message = "username is required.")
                      String username,
                      boolean enabled,
                      @NotEmpty(message = "roles are required.")
                      String roles) implements Serializable {
}