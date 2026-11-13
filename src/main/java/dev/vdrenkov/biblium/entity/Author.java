package dev.vdrenkov.biblium.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "authors", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "surname" }))
public class Author extends Person {
    public Author(final int id, final String name, final String surname) {
        super(id, name, surname);
    }

    public Author(final String name, final String surname) {
        super(name, surname);
    }

    public Author() {
    }
}
