package dev.vdrenkov.biblium.repository;

import dev.vdrenkov.biblium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
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
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT borrowingOrder FROM Order borrowingOrder WHERE borrowingOrder.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") int id);

    List<Order> findByClientId(int clientId);

    List<Order> findByIssueDate(LocalDate date);

    List<Order> findByIssueDateBefore(LocalDate date);

    List<Order> findByIssueDateAfter(LocalDate date);

    List<Order> findByDueDate(LocalDate date);

    List<Order> findByDueDateBefore(LocalDate date);

    List<Order> findByDueDateAfter(LocalDate date);
}
