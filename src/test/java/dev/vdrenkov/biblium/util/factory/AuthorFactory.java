package dev.vdrenkov.biblium.util.factory;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.entity.Author;
import dev.vdrenkov.biblium.request.AuthorRequest;

import java.util.Collections;
import java.util.List;

import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static dev.vdrenkov.biblium.util.constant.TestConstants.NAME;
import static dev.vdrenkov.biblium.util.constant.TestConstants.SURNAME;

public final class AuthorFactory {

    private AuthorFactory() {
        throw new IllegalStateException("Utility class. Must not be instantiated!");
    }

    public static Author getDefaultAuthor() {
        return new Author(ID, NAME, SURNAME);
    }

    public static List<Author> getDefaultAuthorsList() {
        return Collections.singletonList(getDefaultAuthor());
    }

    public static AuthorDto getDefaultAuthorDto() {
        return new AuthorDto(ID, NAME, SURNAME);
    }

    public static List<AuthorDto> getDefaultAuthorsDtoList() {
        return Collections.singletonList(getDefaultAuthorDto());
    }

    public static AuthorRequest getDefaultAuthorRequest() {
        return new AuthorRequest(NAME, SURNAME);
    }
}

