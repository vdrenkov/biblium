package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.util.factory.AuthorFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthorMapperTest {

    @Test
    void testMapAuthorsToAuthorsDto() {
        final List<AuthorDto> authorsDto = AuthorMapper.mapAuthorsToAuthorsDto(AuthorFactory.getDefaultAuthorsList());

        assertNotNull(authorsDto);
    }

    @Test
    void testMapAuthorToAuthorDto() {
        final AuthorDto authorDto = AuthorMapper.mapAuthorToAuthorDto(AuthorFactory.getDefaultAuthor());

        assertNotNull(authorDto);
    }
}
