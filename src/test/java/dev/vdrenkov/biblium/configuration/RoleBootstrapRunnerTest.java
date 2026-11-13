package dev.vdrenkov.biblium.configuration;

import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.entity.UserRole;
import dev.vdrenkov.biblium.repository.UserRepository;
import dev.vdrenkov.biblium.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RoleBootstrapRunnerTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void run_missingRoles_createsBothStaffRoles() {
        when(userRoleRepository.existsByRole("ADMIN")).thenReturn(false);
        when(userRoleRepository.existsByRole("LIBRARIAN")).thenReturn(false);

        final RoleBootstrapRunner runner = new RoleBootstrapRunner(userRoleRepository, userRepository, passwordEncoder,
            true, "", "");

        runner.run();

        final ArgumentCaptor<UserRole> roleCaptor = ArgumentCaptor.forClass(UserRole.class);
        verify(userRoleRepository, times(2)).save(roleCaptor.capture());
        assertEquals(List.of("ADMIN", "LIBRARIAN"), roleCaptor.getAllValues()
            .stream()
            .map(UserRole::getRole)
            .toList());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRun_bootstrapsAdminWhenConfigured() {
        final UserRole adminRole = new UserRole(1, "ADMIN");
        when(userRoleRepository.existsByRole("ADMIN")).thenReturn(true);
        when(userRoleRepository.existsByRole("LIBRARIAN")).thenReturn(true);
        when(userRoleRepository.findUserRoleByRole("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        final RoleBootstrapRunner runner = new RoleBootstrapRunner(userRoleRepository, userRepository, passwordEncoder,
            true, "admin", "password123");

        runner.run();

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        final User savedUser = userCaptor.getValue();
        assertEquals("admin", savedUser.getUsername());
        assertEquals("encoded-password", savedUser.getPassword());
        assertTrue(savedUser.getUserRoles().stream().anyMatch(role -> "ADMIN".equals(role.getRole())));
    }

    @Test
    void testRun_bootstrapDisabled_doesNothing() {
        final RoleBootstrapRunner runner = new RoleBootstrapRunner(userRoleRepository, userRepository, passwordEncoder,
            false, "admin", "password123");

        runner.run();

        verify(userRoleRepository, never()).existsByRole(any());
        verify(userRoleRepository, never()).save(any(UserRole.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRun_adminAlreadyExists_doesNotCreateAgain() {
        when(userRoleRepository.existsByRole("ADMIN")).thenReturn(true);
        when(userRoleRepository.existsByRole("LIBRARIAN")).thenReturn(true);
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        final RoleBootstrapRunner runner = new RoleBootstrapRunner(userRoleRepository, userRepository, passwordEncoder,
            true, "admin", "password123");

        runner.run();

        verify(userRepository, never()).save(any(User.class));
    }
}
