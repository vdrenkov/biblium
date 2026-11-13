package dev.vdrenkov.biblium.configuration;

import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.entity.UserRole;
import dev.vdrenkov.biblium.repository.UserRepository;
import dev.vdrenkov.biblium.repository.UserRoleRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class RoleBootstrapRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(RoleBootstrapRunner.class);
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String LIBRARIAN_ROLE = "LIBRARIAN";

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean bootstrapRoles;
    private final String bootstrapAdminUsername;
    private final String bootstrapAdminPassword;

    public RoleBootstrapRunner(final UserRoleRepository userRoleRepository, final UserRepository userRepository,
        final PasswordEncoder passwordEncoder, @Value("${app.bootstrap.roles:true}") final boolean bootstrapRoles,
        @Value("${app.bootstrap.admin.username:}") final String bootstrapAdminUsername,
        @Value("${app.bootstrap.admin.password:}") final String bootstrapAdminPassword) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapRoles = bootstrapRoles;
        this.bootstrapAdminUsername = bootstrapAdminUsername;
        this.bootstrapAdminPassword = bootstrapAdminPassword;
    }

    @Override
    @Transactional
    public void run(final String @NonNull ... args) {
        if (!bootstrapRoles) {
            log.info("Role bootstrap disabled by configuration");
            return;
        }

        ensureRoleExists(ADMIN_ROLE);
        ensureRoleExists(LIBRARIAN_ROLE);
        bootstrapAdminUserIfConfigured();
    }

    private void ensureRoleExists(final String roleName) {
        if (!userRoleRepository.existsByRole(roleName)) {
            userRoleRepository.save(new UserRole(roleName));
            log.info("Bootstrapped missing '{}' role", roleName);
        }
    }

    private void bootstrapAdminUserIfConfigured() {
        if (!StringUtils.hasText(bootstrapAdminUsername) || !StringUtils.hasText(bootstrapAdminPassword)) {
            return;
        }

        if (userRepository.existsByUsername(bootstrapAdminUsername)) {
            return;
        }

        final UserRole adminRole = userRoleRepository
            .findUserRoleByRole(ADMIN_ROLE)
            .orElseThrow(() -> new IllegalStateException("ADMIN role is missing"));

        final User admin = new User(bootstrapAdminUsername, passwordEncoder.encode(bootstrapAdminPassword),
            List.of(adminRole));

        userRepository.save(admin);
        log.info("Bootstrapped initial admin user '{}'", bootstrapAdminUsername);
    }
}
