package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.UserDto;
import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.request.CreateUserRequest;
import dev.vdrenkov.biblium.request.UserRequest;
import dev.vdrenkov.biblium.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final String USER_PATH = "/users";

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid final UserRequest request) {
        final HttpCookie cookie = userService.login(request);
        log.info("A login request submitted");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @PostMapping(USER_PATH)
    public ResponseEntity<Void> createUser(@RequestBody @Valid final CreateUserRequest request) {
        final User user = userService.createUser(request);
        final URI location = UriComponentsBuilder
            .fromUriString(USER_PATH + "/{id}")
            .buildAndExpand(user.getId())
            .toUri();
        log.info("Staff account created");

        return ResponseEntity.created(location).build();
    }

    @GetMapping(USER_PATH)
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("All users requested from the database");
        return ResponseEntity.ok(userService.getAllUsersDto());
    }

    @GetMapping(USER_PATH + "/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable final int id) {
        log.info("User with an ID {} requested from the database.", id);
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }
}
