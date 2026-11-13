package dev.vdrenkov.biblium.request;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthorRequest extends PersonRequest {
    public AuthorRequest(final String name, final String surname) {
        super(name, surname);
    }
}
