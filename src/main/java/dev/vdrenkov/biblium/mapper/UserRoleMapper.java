package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.UserRoleDto;
import dev.vdrenkov.biblium.entity.UserRole;

import java.util.Comparator;
import java.util.List;

public final class UserRoleMapper {

    private UserRoleMapper() {
    }

    public static List<UserRoleDto> mapUserRolesToUserRolesDto(final List<UserRole> userRoles) {
        return userRoles.stream()
            .map(UserRoleMapper::mapUserRoleToUserRoleDto)
            .sorted(Comparator.comparing(UserRoleDto::role))
            .toList();
    }

    public static UserRoleDto mapUserRoleToUserRoleDto(final UserRole userRole) {
        return new UserRoleDto(userRole.getId(), userRole.getRole());
    }
}
