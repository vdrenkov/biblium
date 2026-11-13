package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookRequest {

    @Pattern(regexp = "^[A-Z0-9][A-Z0-9a-z .]*$",
        message = "The book name must start with a capital letter or digit and should contain only letters, "
            + "digits, spaces and/or dots")
    @NotNull(message = "The name cannot be null")
    private String name;

    @NotNull(message = "The publishing date cannot be null")
    private LocalDate publishDate;

    @Positive(message = "The author ID must be a positive digit")
    @NotNull(message = "The author ID cannot be null")
    private Integer authorId;

    @Positive(message = "The quantity must be a positive digit")
    @NotNull(message = "The quantity cannot be null")
    private Integer quantity;
}
