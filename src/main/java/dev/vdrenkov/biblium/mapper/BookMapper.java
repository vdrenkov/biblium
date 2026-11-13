package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.BookDto;
import dev.vdrenkov.biblium.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BookMapper {
    private static final Logger log = LoggerFactory.getLogger(BookMapper.class);

    private BookMapper() {
        /* This utility class should not be instantiated */
    }

    public static List<BookDto> mapBooksToBooksDto(final List<Book> books) {
        final List<BookDto> booksDto = new ArrayList<>();

        for (final Book book : books) {
            booksDto.add(mapBookToBookDto(book));
        }

        booksDto.sort(Comparator.comparing(BookDto::id));
        log.debug("Books' list mapped to books' DTOs list");
        return booksDto;
    }

    public static BookDto mapBookToBookDto(final Book book) {
        log.debug("Book mapped to book DTO");
        return new BookDto(book.getId(), book.getName(), book.getPublishDate(),
            AuthorMapper.mapAuthorToAuthorDto(book.getAuthor()), book.getQuantity());
    }
}
