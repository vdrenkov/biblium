package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.UserDto;
import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.exception.UserNotFoundException;
import dev.vdrenkov.biblium.jwt.JwtCookieUtil;
import dev.vdrenkov.biblium.mapper.UserMapper;
import dev.vdrenkov.biblium.repository.UserRepository;
import dev.vdrenkov.biblium.request.CreateUserRequest;
import dev.vdrenkov.biblium.request.UserRequest;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    public UserService(final AuthenticationManager authenticationManager, final PasswordEncoder passwordEncoder,
        final JwtCookieUtil jwtCookieUtil, final UserRepository userRepository, final UserRoleService userRoleService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtCookieUtil = jwtCookieUtil;
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    @Transactional
    public User createUser(final CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("A user with this username already exists");
        }

        final var roles = request.getRolesIds()
            .stream()
            .distinct()
            .map(userRoleService::getUserRoleById)
            .toList();
        final String password = passwordEncoder.encode(request.getPassword());
        return userRepository.save(new User(request.getUsername(), password, roles));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsersDto() {
        return UserMapper.mapUsersToUsersDto(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(final int id) {
        final User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return UserMapper.mapUserToUserDto(user);
    }

    public HttpCookie login(final UserRequest request) {
        final var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        return jwtCookieUtil.createJWTCookie((UserDetails) authentication.getPrincipal());
    }
}
