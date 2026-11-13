package dev.vdrenkov.biblium.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Getter
public class ClientRequest extends PersonRequest {

    @Pattern(regexp = "^\\d{9,10}$", message = "The phone number must consist of only 9 or 10 digits.")
    @NotNull(message = "The phone number cannot be null")
    private String phoneNumber;
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email pattern")
    @NotNull(message = "The email cannot be null")
    private String email;

    public ClientRequest(final String name, final String surname, final String phoneNumber, final String email) {
        super(name, surname);
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final ClientRequest that))
            return false;
        if (!super.equals(o))
            return false;
        return Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), phoneNumber, email);
    }
}
