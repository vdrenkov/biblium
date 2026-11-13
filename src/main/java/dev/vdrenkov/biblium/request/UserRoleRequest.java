package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserRoleRequest(
    @Pattern(regexp = "^[A-Z_]+$", message = "The role should contain only capital letters and/or underscore/s.")
    @NotNull(message = "The role cannot be null")
    String role) {
}
