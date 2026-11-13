package dev.vdrenkov.biblium.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client client;

    @ManyToMany
    @JoinTable(name = "order_books", joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> books;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    public Order(final Client client, final List<Book> books, final LocalDate issueDate, final LocalDate dueDate) {
        this.client = client;
        this.books = books;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Order() {
    }

    public Order(final int id, final Client client, final List<Book> books, final LocalDate issueDate, final LocalDate dueDate) {
        this.id = id;
        this.client = client;
        this.books = books;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public List<Book> getBooks() {
        return books;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
