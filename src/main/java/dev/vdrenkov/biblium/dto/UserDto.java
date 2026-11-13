package dev.vdrenkov.biblium.dto;

import java.util.List;

public record UserDto(int id, String username, List<UserRoleDto> userRoleDtos) {
}
