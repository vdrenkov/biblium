package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.BookDto;
import dev.vdrenkov.biblium.entity.Book;
import dev.vdrenkov.biblium.request.BookRequest;
import dev.vdrenkov.biblium.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private static final String URI = "/books";

    private final BookService bookService;

    public BookController(final BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(URI)
    public ResponseEntity<Void> addBook(@RequestBody @Valid final BookRequest bookRequest) {
        final Book book = bookService.addBook(bookRequest);

        final URI location = UriComponentsBuilder.fromUriString("/books/{id}").buildAndExpand(book.getId()).toUri();
        log.info("A new book added");
        return ResponseEntity.created(location).build();
    }

    @GetMapping(URI)
    public ResponseEntity<List<BookDto>> getAllAvailableBooks() {
        log.info("All available books requested from the database");
        return ResponseEntity.ok(bookService.getAllAvailableBooksDto());
    }

    @GetMapping("/authors/{authorId}" + URI)
    public ResponseEntity<List<BookDto>> getAllBooksByAuthor(@PathVariable final int authorId) {
        log.info("All books with author ID {} requested from the database.", authorId);
        return ResponseEntity.ok(bookService.getAllBooksDtoByAuthor(authorId));
    }

    @GetMapping(URI + "/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable final int id) {
        log.info("Book with an ID {} requested from the database.", id);
        return ResponseEntity.ok(bookService.getBookDtoById(id));
    }
}
