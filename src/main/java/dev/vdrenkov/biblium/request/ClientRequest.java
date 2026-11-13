package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClientRequest(
    @Pattern(regexp = "^[A-Z][a-z. ]+$",
        message = "The name should contain only letters, spaces and/or dots and should start with a capital letter.")
    @NotNull(message = "The name cannot be null")
    String name,
    @Pattern(regexp = "^[A-Z][a-z. ]+$",
        message = "The name should contain only letters, spaces and/or dots and should start with a capital letter.")
    @NotNull(message = "The surname cannot be null")
    String surname,


    @Pattern(regexp = "^\\d{9,10}$", message = "The phone number must consist of only 9 or 10 digits.")
    @NotNull(message = "The phone number cannot be null")
    String phoneNumber,
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email pattern")
    @NotNull(message = "The email cannot be null")
    String email) {
}
