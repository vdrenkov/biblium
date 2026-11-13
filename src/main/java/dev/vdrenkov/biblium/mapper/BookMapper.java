package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.BookDto;
import dev.vdrenkov.biblium.entity.Book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BookMapper {

    private BookMapper() {
    }

    public static List<BookDto> mapBooksToBooksDto(final List<Book> books) {
        final List<BookDto> booksDto = new ArrayList<>();

        for (final Book book : books) {
            booksDto.add(mapBookToBookDto(book));
        }

        booksDto.sort(Comparator.comparing(BookDto::id));
        return booksDto;
    }

    public static BookDto mapBookToBookDto(final Book book) {
        return new BookDto(book.getId(), book.getName(), book.getPublishDate(),
            AuthorMapper.mapAuthorToAuthorDto(book.getAuthor()), book.getQuantity());
    }
}
