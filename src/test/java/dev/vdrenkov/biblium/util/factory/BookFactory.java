package dev.vdrenkov.biblium.util.factory;

import dev.vdrenkov.biblium.dto.BookDto;
import dev.vdrenkov.biblium.entity.Book;
import dev.vdrenkov.biblium.request.BookRequest;

import java.util.Collections;
import java.util.List;

import static dev.vdrenkov.biblium.util.constant.TestConstants.BOOKS_NAMES_LIST_VALUE;
import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static dev.vdrenkov.biblium.util.constant.TestConstants.LOCAL_DATE;
import static dev.vdrenkov.biblium.util.constant.TestConstants.NAME;
import static dev.vdrenkov.biblium.util.constant.TestConstants.QUANTITY;

public final class BookFactory {

    private BookFactory() {
        throw new IllegalStateException("Utility class. Must not be instantiated!");
    }

    public static Book getDefaultBook() {
        return new Book(ID, NAME, LOCAL_DATE, AuthorFactory.getDefaultAuthor(), QUANTITY);
    }

    public static List<Book> getDefaultBooksList() {
        return Collections.singletonList(getDefaultBook());
    }

    public static BookDto getDefaultBookDto() {
        return new BookDto(ID, NAME, LOCAL_DATE, AuthorFactory.getDefaultAuthorDto(), QUANTITY);
    }

    public static List<BookDto> getDefaultBooksDtoList() {
        return Collections.singletonList(getDefaultBookDto());
    }

    public static BookRequest getDefaultBookRequest() {
        return new BookRequest(NAME, LOCAL_DATE, ID, QUANTITY);
    }

    public static List<String> getDefaultBooksNamesList() {
        return Collections.singletonList(BOOKS_NAMES_LIST_VALUE);
    }

    public static List<Integer> getDefaultBooksIdsList() {
        return Collections.singletonList(1);
    }
}

