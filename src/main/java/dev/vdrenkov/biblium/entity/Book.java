package dev.vdrenkov.biblium.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Author author;

    @Column(name = "quantity")
    private int quantity;

    public Book(String name, LocalDate publishDate, Author author, int quantity) {
        this.name = name;
        this.publishDate = publishDate;
        this.author = author;
        this.quantity = quantity;
    }

    public Book() {
    }

    public Book(final int id, final String name, final LocalDate publishDate, final Author author, final int quantity) {
        this.id = id;
        this.name = name;
        this.publishDate = publishDate;
        this.author = author;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public Author getAuthor() {
        return author;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }
}
