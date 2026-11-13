package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserRequest {

    @Pattern(regexp = "^[A-Za-z0-9_-]+$",
        message = "The username must consist of letters, numbers, dashes and/or underscores.")
    @NotBlank(message = "The username cannot be blank")
    @Size(min = 3, max = 50, message = "The username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "The password cannot be blank")
    @Size(min = 8, max = 100, message = "The password must be between 8 and 100 characters")
    private String password;

    @NotEmpty(message = "At least one role ID is required")
    @NotNull(message = "The roles' IDs cannot be null")
    private List<@NotNull(message = "Role ID cannot be null") @Positive(
        message = "Role ID must be a positive digit") Integer> rolesIds;
}
