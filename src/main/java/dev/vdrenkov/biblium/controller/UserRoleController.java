package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.UserRoleDto;
import dev.vdrenkov.biblium.entity.UserRole;
import dev.vdrenkov.biblium.request.UserRoleRequest;
import dev.vdrenkov.biblium.service.UserRoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(final UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping
    public ResponseEntity<Void> addUserRole(@RequestBody @Valid final UserRoleRequest userRequest) {
        final UserRole userRole = userRoleService.addUserRole(userRequest);

        final URI location = UriComponentsBuilder.fromUriString("/roles/{id}").buildAndExpand(userRole.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<UserRoleDto>> getAllUserRoles() {
        return ResponseEntity.ok(userRoleService.getAllUserRolesDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRoleDto> getUserRoleById(@PathVariable final int id) {
        return ResponseEntity.ok(userRoleService.getUserRoleDtoById(id));
    }
}
