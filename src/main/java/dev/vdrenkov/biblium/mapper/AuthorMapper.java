package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.entity.Author;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AuthorMapper {

    private AuthorMapper() {
    }

    public static List<AuthorDto> mapAuthorsToAuthorsDto(final List<Author> authors) {
        final List<AuthorDto> authorsDto = new ArrayList<>();

        for (final Author author : authors) {
            authorsDto.add(mapAuthorToAuthorDto(author));
        }

        authorsDto.sort(Comparator.comparing(AuthorDto::id));
        return authorsDto;
    }

    public static AuthorDto mapAuthorToAuthorDto(final Author author) {
        return new AuthorDto(author.getId(), author.getName(), author.getSurname());
    }
}
