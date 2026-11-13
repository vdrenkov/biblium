package dev.vdrenkov.biblium.dto;

import java.time.LocalDate;

public record BookDto(int id, String name, LocalDate publishDate, AuthorDto authorDto, int quantity) {
}
