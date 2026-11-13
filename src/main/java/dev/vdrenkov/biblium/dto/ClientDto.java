package dev.vdrenkov.biblium.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ClientDto extends PersonDto {
    private final String phoneNumber;
    private final String email;

    public ClientDto(final int id, final String name, final String surname, final String phoneNumber,
        final String email) {
        super(id, name, surname);
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final ClientDto clientDto))
            return false;
        if (!super.equals(o))
            return false;
        return Objects.equals(phoneNumber, clientDto.phoneNumber) && Objects.equals(email, clientDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), phoneNumber, email);
    }
}
