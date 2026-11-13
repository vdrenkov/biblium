package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.UserDto;
import dev.vdrenkov.biblium.entity.User;

import java.util.Comparator;
import java.util.List;

public final class UserMapper {

    private UserMapper() {
    }

    public static List<UserDto> mapUsersToUsersDto(final List<User> users) {
        return users.stream()
            .map(UserMapper::mapUserToUserDto)
            .sorted(Comparator.comparing(UserDto::username))
            .toList();
    }

    public static UserDto mapUserToUserDto(final User user) {
        return new UserDto(user.getId(), user.getUsername(), UserRoleMapper.mapUserRolesToUserRolesDto(user.getUserRoles()));
    }
}
