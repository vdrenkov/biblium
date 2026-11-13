package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record BookRequest(
    @Pattern(regexp = "^[A-Z0-9][A-Z0-9a-z .]*$",
        message = "The book name must start with a capital letter or digit and should contain only letters, "
            + "digits, spaces and/or dots")
    @NotNull(message = "The name cannot be null")
    String name,

    @NotNull(message = "The publishing date cannot be null")
    LocalDate publishDate,

    @Positive(message = "The author ID must be a positive digit")
    @NotNull(message = "The author ID cannot be null")
    Integer authorId,

    @Positive(message = "The quantity must be a positive digit")
    @NotNull(message = "The quantity cannot be null")
    Integer quantity) {
}
