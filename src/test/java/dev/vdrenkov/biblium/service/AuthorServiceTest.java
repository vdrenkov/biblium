package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.entity.Author;
import dev.vdrenkov.biblium.mapper.AuthorMapper;
import dev.vdrenkov.biblium.repository.AuthorRepository;
import dev.vdrenkov.biblium.util.factory.AuthorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @InjectMocks
    private AuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Test
    void testAddAuthor() {
        when(authorRepository.save(any())).thenReturn(new Author());

        final Author author = authorService.addAuthor(AuthorFactory.getDefaultAuthorRequest());

        Assertions.assertNotNull(author);
    }

    @Test
    void testGetAllAuthors() {
        when(authorRepository.findAll()).thenReturn(AuthorFactory.getDefaultAuthorsList());

        final List<Author> result = authorService.getAllAuthors();

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAllAuthorsDto() {
        when(authorRepository.findAll()).thenReturn(AuthorFactory.getDefaultAuthorsList());

        final List<AuthorDto> result = authorService.getAllAuthorsDto();

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAuthorById() {
        when(authorRepository.findById(anyInt())).thenReturn(Optional.of(AuthorFactory.getDefaultAuthor()));

        final Author result = authorService.getAuthorById(ID);

        assertNotNull(result.getName());
    }

    @Test
    void testGetAuthorDtoById() {
        when(authorRepository.findById(anyInt())).thenReturn(Optional.of(AuthorFactory.getDefaultAuthor()));

        final AuthorDto result = authorService.getAuthorDtoById(ID);

        assertNotNull(result.name());
    }
}

