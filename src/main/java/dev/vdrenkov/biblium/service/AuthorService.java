package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.entity.Author;
import dev.vdrenkov.biblium.exception.AuthorNotFoundException;
import dev.vdrenkov.biblium.mapper.AuthorMapper;
import dev.vdrenkov.biblium.repository.AuthorRepository;
import dev.vdrenkov.biblium.request.AuthorRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    public AuthorService(final AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author addAuthor(final AuthorRequest authorRequest) {
        final Author author = new Author(authorRequest.getName(), authorRequest.getSurname());

        log.info("Trying to add a new author");
        return authorRepository.save(author);
    }

    public List<Author> getAllAuthors() {
        log.info("Trying to retrieve all authors");
        return authorRepository.findAll();
    }

    public List<AuthorDto> getAllAuthorsDto() {
        return AuthorMapper.mapAuthorsToAuthorsDto(getAllAuthors());
    }

    public Author getAuthorById(final int id) {
        log.info("Trying to retrieve author with an ID {}", id);
        return authorRepository.findById(id).orElseThrow(AuthorNotFoundException::new);
    }

    public AuthorDto getAuthorDtoById(final int id) {
        return AuthorMapper.mapAuthorToAuthorDto(getAuthorById(id));
    }
}
