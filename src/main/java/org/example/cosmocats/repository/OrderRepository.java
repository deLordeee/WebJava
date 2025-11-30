package org.example.cosmocats.repository;

import org.example.cosmocats.repository.entity.OrderEntity;
import org.example.cosmocats.common.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    List<OrderEntity> findByStatus(OrderStatus status);
    List<OrderEntity> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM OrderEntity o WHERE o.totalAmount > :minAmount ORDER BY o.totalAmount DESC")
    List<OrderEntity> findOrdersWithTotalAmountGreaterThan(@Param("minAmount") BigDecimal minAmount);

    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status AND o.orderDate >= :sinceDate")
    List<OrderEntity> findRecentOrdersByStatus(@Param("status") OrderStatus status,
                                               @Param("sinceDate") LocalDateTime sinceDate);
}