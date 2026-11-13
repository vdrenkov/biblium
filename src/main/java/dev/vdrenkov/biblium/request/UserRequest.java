package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRequest {

    @NotBlank(message = "The username cannot be blank")
    @Size(min = 3, max = 50, message = "The username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "The password cannot be blank")
    @Size(min = 8, max = 100, message = "The password must be between 8 and 100 characters")
    private String password;
}
