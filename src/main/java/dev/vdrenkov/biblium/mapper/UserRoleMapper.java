package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.UserRoleDto;
import dev.vdrenkov.biblium.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class UserRoleMapper {
    private static final Logger log = LoggerFactory.getLogger(UserRoleMapper.class);

    private UserRoleMapper() {
        /* This utility class should not be instantiated */
    }

    public static List<UserRoleDto> mapUserRolesToUserRolesDto(final List<UserRole> userRoles) {
        final List<UserRoleDto> userRolesDto = new ArrayList<>();

        for (final UserRole userRole : userRoles) {
            userRolesDto.add(mapUserRoleToUserRoleDto(userRole));
        }

        userRolesDto.sort(Comparator.comparing(UserRoleDto::getRole));
        log.debug("Users' list mapped to users' DTOs list");
        return userRolesDto;
    }

    public static UserRoleDto mapUserRoleToUserRoleDto(final UserRole userRole) {
        log.debug("User role mapped to user role DTO");
        return new UserRoleDto(userRole.getRole());
    }
}
