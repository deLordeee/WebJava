package org.example.cosmocats.service.impl;

import org.example.cosmocats.repository.entity.OrderEntity;
import org.example.cosmocats.common.OrderStatus;
import org.example.cosmocats.repository.OrderRepository;
import org.example.cosmocats.service.OrderService;
import org.example.cosmocats.service.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderEntity createOrder(OrderEntity order) {

        if (order.getOrderNumber() == null || order.getOrderNumber().isBlank()) {
            order.setOrderNumber(generateOrderNumber());
        }


        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }


        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderEntity> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderEntity> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> getRecentOrders(LocalDateTime sinceDate) {
        return orderRepository.findByOrderDateBetween(sinceDate, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersWithTotalGreaterThan(BigDecimal minAmount) {
        return orderRepository.findOrdersWithTotalAmountGreaterThan(minAmount);
    }

    @Override
    public OrderEntity updateOrderStatus(Long orderId, OrderStatus status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}