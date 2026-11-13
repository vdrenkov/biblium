package dev.vdrenkov.biblium.repository;

import dev.vdrenkov.biblium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByClientId(int clientId);

    List<Order> findByIssueDate(LocalDate date);

    List<Order> findByIssueDateBefore(LocalDate date);

    List<Order> findByIssueDateAfter(LocalDate date);

    List<Order> findByDueDate(LocalDate date);

    List<Order> findByDueDateBefore(LocalDate date);

    List<Order> findByDueDateAfter(LocalDate date);
}
