package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.entity.UserRole;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserMapperTest {
    @Test
    void mapUserToUserDto_staffAccount_exposesIdsWithoutPassword() {
        final User user = new User(7, "librarian", "encoded-password", List.of(new UserRole(2, "LIBRARIAN")));
        final var json = new ObjectMapper().valueToTree(UserMapper.mapUserToUserDto(user));

        assertEquals(7, json.path("id").asInt());
        assertEquals("librarian", json.path("username").asString());
        assertEquals(2, json.path("userRoleDtos").get(0).path("id").asInt());
        assertFalse(json.has("password"));
    }

    @Test
    void mapUserRoleToUserRoleDto_staffRole_exposesIdForAccountCreation() {
        final var json = new ObjectMapper().valueToTree(UserRoleMapper.mapUserRoleToUserRoleDto(new UserRole(2, "LIBRARIAN")));

        assertEquals(2, json.path("id").asInt());
        assertEquals("LIBRARIAN", json.path("role").asString());
    }
}
