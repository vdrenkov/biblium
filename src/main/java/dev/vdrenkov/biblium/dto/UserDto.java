package dev.vdrenkov.biblium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UserDto {
    private String username;
    private List<UserRoleDto> userRoleDtos;
}
