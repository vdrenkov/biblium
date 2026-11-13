package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthorRequest(
    @Pattern(regexp = "^[A-Z][a-z. ]+$",
        message = "The name should contain only letters, spaces and/or dots and should start with a capital letter.")
    @NotNull(message = "The name cannot be null")
    String name,
    @Pattern(regexp = "^[A-Z][a-z. ]+$",
        message = "The name should contain only letters, spaces and/or dots and should start with a capital letter.")
    @NotNull(message = "The surname cannot be null")
    String surname) {
}
