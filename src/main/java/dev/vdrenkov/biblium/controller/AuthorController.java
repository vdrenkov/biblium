package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.AuthorDto;
import dev.vdrenkov.biblium.entity.Author;
import dev.vdrenkov.biblium.request.AuthorRequest;
import dev.vdrenkov.biblium.service.AuthorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;

    public AuthorController(final AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<Void> addAuthor(@RequestBody @Valid final AuthorRequest authorRequest) {
        final Author author = authorService.addAuthor(authorRequest);

        final URI location = UriComponentsBuilder.fromUriString("/authors/{id}").buildAndExpand(author.getId()).toUri();
        log.info("A new author added");
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        log.info("All authors requested from the database");
        return ResponseEntity.ok(authorService.getAllAuthorsDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable final int id) {
        log.info("Author with an ID {} requested from the database.", id);
        return ResponseEntity.ok(authorService.getAuthorDtoById(id));
    }
}
