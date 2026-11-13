package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonRequest {

    @Pattern(regexp = "^[A-Z][a-z. ]+$",
        message = "The name should contain only letters, spaces and/or dots and should start with a capital letter.")
    @NotNull(message = "The name cannot be null")
    private String name;
    @Pattern(regexp = "^[A-Z][a-z. ]+$",
        message = "The name should contain only letters, spaces and/or dots and should start with a capital letter.")
    @NotNull(message = "The surname cannot be null")
    private String surname;
}
