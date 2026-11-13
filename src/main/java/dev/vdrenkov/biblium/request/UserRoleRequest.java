package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleRequest {

    @Pattern(regexp = "^[A-Z_]+$", message = "The role should contain only capital letters and/or underscore/s.")
    @NotNull(message = "The role cannot be null")
    private String role;
}
