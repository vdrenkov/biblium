package dev.vdrenkov.biblium.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderDto(int id, ClientDto clientDto, List<String> booksNames, LocalDate issueDate, LocalDate dueDate) {
}
