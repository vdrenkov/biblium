package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.UserRoleDto;
import dev.vdrenkov.biblium.entity.UserRole;
import dev.vdrenkov.biblium.exception.UserRoleNotFoundException;
import dev.vdrenkov.biblium.mapper.UserRoleMapper;
import dev.vdrenkov.biblium.repository.UserRoleRepository;
import dev.vdrenkov.biblium.request.UserRoleRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

    private final UserRoleRepository userRepository;

    public UserRoleService(final UserRoleRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRole addUserRole(final UserRoleRequest userRoleRequest) {
        final UserRole userRole = new UserRole(userRoleRequest.role());

        return userRepository.save(userRole);
    }

    public List<UserRole> getAllUserRoles() {
        return userRepository.findAll();
    }

    public List<UserRoleDto> getAllUserRolesDto() {
        return UserRoleMapper.mapUserRolesToUserRolesDto(getAllUserRoles());
    }

    public UserRole getUserRoleById(final int id) {
        return userRepository.findById(id).orElseThrow(UserRoleNotFoundException::new);
    }

    public UserRoleDto getUserRoleDtoById(final int id) {
        return UserRoleMapper.mapUserRoleToUserRoleDto(getUserRoleById(id));
    }

    public UserRole getUserRoleByRole(final String role) {
        return userRepository.findUserRoleByRole(role).orElseThrow(UserRoleNotFoundException::new);
    }
}
