package dev.vdrenkov.biblium.repository;

import dev.vdrenkov.biblium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Override
    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT borrowingOrder FROM Order borrowingOrder WHERE borrowingOrder.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") int id);

    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findByClientId(int clientId);

    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findByIssueDate(LocalDate date);

    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findByIssueDateBefore(LocalDate date);

    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findByIssueDateAfter(LocalDate date);

    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findByDueDate(LocalDate date);

    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findByDueDateBefore(LocalDate date);

    @EntityGraph(attributePaths = { "client", "books" })
    List<Order> findByDueDateAfter(LocalDate date);
}
