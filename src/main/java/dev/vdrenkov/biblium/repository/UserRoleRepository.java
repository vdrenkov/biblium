package dev.vdrenkov.biblium.repository;

import dev.vdrenkov.biblium.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findUserRoleByRole(String role);

    boolean existsByRole(String role);
}
