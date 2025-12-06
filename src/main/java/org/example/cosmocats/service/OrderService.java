package org.example.cosmocats.service;

import org.example.cosmocats.repository.entity.OrderEntity;
import org.example.cosmocats.common.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderEntity createOrder(OrderEntity order);

    Optional<OrderEntity> getOrderById(Long orderId);

    Optional<OrderEntity> getOrderByNumber(String orderNumber);

    List<OrderEntity> getOrdersByStatus(OrderStatus status);

    List<OrderEntity> getRecentOrders(LocalDateTime sinceDate);

    List<OrderEntity> getOrdersWithTotalGreaterThan(BigDecimal minAmount);

    OrderEntity updateOrderStatus(Long orderId, OrderStatus status);

    void deleteOrder(Long orderId);
}