package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(
    @Positive(message = "The client ID must be a positive digit")
    @NotNull(message = "The client ID cannot be null")
    Integer clientId,

    @NotEmpty(message = "At least one book ID is required")
    @NotNull(message = "The books' IDs cannot be null")
    List<@NotNull(message = "Book ID cannot be null") @Positive(
        message = "Book ID must be a positive digit") Integer> booksIds) {
}
