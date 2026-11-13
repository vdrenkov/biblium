package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.entity.UserRole;
import dev.vdrenkov.biblium.exception.UserRoleNotFoundException;
import dev.vdrenkov.biblium.jwt.JwtCookieUtil;
import dev.vdrenkov.biblium.repository.UserRepository;
import dev.vdrenkov.biblium.request.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtCookieUtil jwtCookieUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleService userRoleService;

    @Test
    void createUser_validStaffAccount_encodesPasswordAndAssignsRequestedRoles() {
        final UserRole adminRole = new UserRole(1, "ADMIN");
        final UserRole librarianRole = new UserRole(2, "LIBRARIAN");
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRoleService.getUserRoleById(1)).thenReturn(adminRole);
        when(userRoleService.getUserRoleById(2)).thenReturn(librarianRole);

        userService.createUser(new CreateUserRequest("staff", "password123", List.of(1, 2)));

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        final User savedUser = userCaptor.getValue();
        assertEquals("staff", savedUser.getUsername());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(List.of(adminRole, librarianRole), savedUser.getUserRoles());
        verifyNoInteractions(authenticationManager, jwtCookieUtil);
    }

    @Test
    void createUser_duplicateUsername_doesNotSaveOrEncodePassword() {
        when(userRepository.existsByUsername("staff")).thenReturn(true);
        final var request = new CreateUserRequest("staff", "password123", List.of(2));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, authenticationManager, jwtCookieUtil, userRoleService);
    }

    @Test
    void createUser_duplicateRoleIds_assignsRoleOnce() {
        final UserRole librarianRole = new UserRole(2, "LIBRARIAN");
        when(userRoleService.getUserRoleById(2)).thenReturn(librarianRole);
        final var request = new CreateUserRequest("staff", "password123", List.of(2, 2));

        userService.createUser(request);

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(List.of(librarianRole), userCaptor.getValue().getUserRoles());
        verify(userRoleService).getUserRoleById(2);
    }

    @Test
    void createUser_unknownRole_doesNotSaveOrEncodePassword() {
        when(userRoleService.getUserRoleById(99)).thenThrow(new UserRoleNotFoundException());
        final var request = new CreateUserRequest("staff", "password123", List.of(99));

        assertThrows(UserRoleNotFoundException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, authenticationManager, jwtCookieUtil);
    }
}
