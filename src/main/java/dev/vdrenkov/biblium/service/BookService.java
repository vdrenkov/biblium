package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.BookDto;
import dev.vdrenkov.biblium.entity.Author;
import dev.vdrenkov.biblium.entity.Book;
import dev.vdrenkov.biblium.exception.BookNotFoundException;
import dev.vdrenkov.biblium.mapper.BookMapper;
import dev.vdrenkov.biblium.repository.BookRepository;
import dev.vdrenkov.biblium.request.BookRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final AuthorService authorService;
    private final BookRepository bookRepository;

    public BookService(final AuthorService authorService, final BookRepository bookRepository) {
        this.authorService = authorService;
        this.bookRepository = bookRepository;
    }

    public Book addBook(final BookRequest bookRequest) {
        final Author bookAuthor = authorService.getAuthorById(bookRequest.authorId());

        return bookRepository.save(
            new Book(bookRequest.name(), bookRequest.publishDate(), bookAuthor, bookRequest.quantity()));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAllAvailableBooks() {
        return bookRepository.findByQuantityGreaterThan(0);
    }

    public List<BookDto> getAllAvailableBooksDto() {
        return BookMapper.mapBooksToBooksDto(getAllAvailableBooks());
    }

    public List<Book> getAllBooksByAuthor(final int authorId) {
        return bookRepository.findByAuthorIdAndQuantityGreaterThan(authorId, 0);
    }

    public List<BookDto> getAllBooksDtoByAuthor(final int authorId) {
        return BookMapper.mapBooksToBooksDto(getAllBooksByAuthor(authorId));
    }

    public Book getBookById(final int id) {
        return bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    }

    public BookDto getBookDtoById(final int id) {
        return BookMapper.mapBookToBookDto(getBookById(id));
    }

    @Transactional
    public Book decreaseBookQuantity(final int id) {
        final Book book = bookRepository.findByIdForUpdate(id).orElseThrow(BookNotFoundException::new);
        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("Book with ID " + id + " is out of stock");
        }

        book.setQuantity(book.getQuantity() - 1);

        return bookRepository.save(book);
    }
}
