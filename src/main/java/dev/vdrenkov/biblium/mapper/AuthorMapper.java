package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.entity.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AuthorMapper {
    private static final Logger log = LoggerFactory.getLogger(AuthorMapper.class);

    private AuthorMapper() {
        /* This utility class should not be instantiated */
    }

    public static List<AuthorDto> mapAuthorsToAuthorsDto(final List<Author> authors) {
        final List<AuthorDto> authorsDto = new ArrayList<>();

        for (final Author author : authors) {
            authorsDto.add(mapAuthorToAuthorDto(author));
        }

        authorsDto.sort(Comparator.comparing(AuthorDto::getId));
        log.debug("Authors' list mapped to authors' DTOs list");
        return authorsDto;
    }

    public static AuthorDto mapAuthorToAuthorDto(final Author author) {
        log.debug("Author mapped to author DTO");
        return new AuthorDto(author.getId(), author.getName(), author.getSurname());
    }
}
