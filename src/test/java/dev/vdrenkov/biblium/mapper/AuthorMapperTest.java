package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.util.factory.AuthorFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorMapperTest {

    @Test
    void mapAuthorsToAuthorsDto_authorList_preservesAllFields() {
        final List<AuthorDto> authorsDto = AuthorMapper.mapAuthorsToAuthorsDto(AuthorFactory.getDefaultAuthorsList());

        assertEquals(AuthorFactory.getDefaultAuthorsDtoList(), authorsDto);
    }

    @Test
    void mapAuthorToAuthorDto_author_preservesAllFields() {
        final AuthorDto authorDto = AuthorMapper.mapAuthorToAuthorDto(AuthorFactory.getDefaultAuthor());

        assertEquals(AuthorFactory.getDefaultAuthorDto(), authorDto);
    }
}
