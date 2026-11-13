package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.UserRoleDto;
import dev.vdrenkov.biblium.entity.UserRole;
import dev.vdrenkov.biblium.request.UserRoleRequest;
import dev.vdrenkov.biblium.service.UserRoleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(UserRoleController.class);

    private final UserRoleService userRoleService;

    public UserRoleController(final UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping
    public ResponseEntity<Void> addUserRole(@RequestBody @Valid final UserRoleRequest userRequest) {
        final UserRole userRole = userRoleService.addUserRole(userRequest);

        final URI location = UriComponentsBuilder.fromUriString("/roles/{id}").buildAndExpand(userRole.getId()).toUri();
        log.info("New role added");
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<UserRoleDto>> getAllUserRoles() {
        log.info("All roles requested from the database");
        return ResponseEntity.ok(userRoleService.getAllUserRolesDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRoleDto> getUserRoleById(@PathVariable final int id) {
        log.info("Role with an ID {} requested from the database.", id);
        return ResponseEntity.ok(userRoleService.getUserRoleDtoById(id));
    }
}
