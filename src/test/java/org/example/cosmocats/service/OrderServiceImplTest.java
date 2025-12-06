package org.example.cosmocats.service;

import org.example.cosmocats.common.OrderStatus;
import org.example.cosmocats.repository.OrderRepository;
import org.example.cosmocats.repository.entity.OrderEntity;
import org.example.cosmocats.service.exception.OrderNotFoundException;
import org.example.cosmocats.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_WithAllFields_Success() {

        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-12345678");
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("100.00"));

        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setId(1L);
        savedOrder.setOrderNumber(order.getOrderNumber());
        savedOrder.setStatus(order.getStatus());
        savedOrder.setOrderDate(order.getOrderDate());

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);


        OrderEntity result = orderService.createOrder(order);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void createOrder_WithoutOrderNumber_GeneratesOrderNumber() {

        OrderEntity order = new OrderEntity();
        order.setOrderNumber(null);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("50.00"));

        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setId(1L);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);


        OrderEntity result = orderService.createOrder(order);


        assertThat(order.getOrderNumber()).isNotNull();
        assertThat(order.getOrderNumber()).startsWith("ORD-");
        assertThat(order.getOrderNumber()).hasSize(12);
        verify(orderRepository).save(order);
    }

    @Test
    void createOrder_WithBlankOrderNumber_GeneratesOrderNumber() {
        // Given
        OrderEntity order = new OrderEntity();
        order.setOrderNumber("");
        order.setStatus(OrderStatus.PENDING);

        OrderEntity savedOrder = new OrderEntity();
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        // When
        orderService.createOrder(order);

        // Then
        assertThat(order.getOrderNumber()).isNotBlank();
        assertThat(order.getOrderNumber()).startsWith("ORD-");
        verify(orderRepository).save(order);
    }

    @Test
    void createOrder_WithoutStatus_SetsDefaultPending() {

        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-TEST");
        order.setStatus(null);

        OrderEntity savedOrder = new OrderEntity();
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);


        orderService.createOrder(order);


        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository).save(order);
    }

    @Test
    void createOrder_WithoutOrderDate_SetsCurrentDate() {

        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-TEST");
        order.setOrderDate(null);

        OrderEntity savedOrder = new OrderEntity();
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

        orderService.createOrder(order);


        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);
        assertThat(order.getOrderDate()).isNotNull();
        assertThat(order.getOrderDate()).isBetween(beforeCreate, afterCreate);
        verify(orderRepository).save(order);
    }

    @Test
    void getOrderById_WhenExists_ReturnsOrder() {
        // Given
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderNumber("ORD-12345678");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));


        Optional<OrderEntity> result = orderService.getOrderById(1L);


        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getOrderNumber()).isEqualTo("ORD-12345678");
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_WhenNotExists_ReturnsEmpty() {

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());


        Optional<OrderEntity> result = orderService.getOrderById(999L);


        assertThat(result).isEmpty();
        verify(orderRepository).findById(999L);
    }

    @Test
    void getOrderByNumber_WhenExists_ReturnsOrder() {

        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-ABC123");

        when(orderRepository.findByOrderNumber("ORD-ABC123")).thenReturn(Optional.of(order));


        Optional<OrderEntity> result = orderService.getOrderByNumber("ORD-ABC123");


        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo("ORD-ABC123");
        verify(orderRepository).findByOrderNumber("ORD-ABC123");
    }

    @Test
    void getOrderByNumber_WhenNotExists_ReturnsEmpty() {

        when(orderRepository.findByOrderNumber("NON-EXISTENT")).thenReturn(Optional.empty());


        Optional<OrderEntity> result = orderService.getOrderByNumber("NON-EXISTENT");


        assertThat(result).isEmpty();
        verify(orderRepository).findByOrderNumber("NON-EXISTENT");
    }

    @Test
    void getOrdersByStatus_ReturnsMatchingOrders() {

        OrderEntity order1 = new OrderEntity();
        order1.setStatus(OrderStatus.PENDING);
        OrderEntity order2 = new OrderEntity();
        order2.setStatus(OrderStatus.PENDING);

        List<OrderEntity> pendingOrders = Arrays.asList(order1, order2);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);


        List<OrderEntity> result = orderService.getOrdersByStatus(OrderStatus.PENDING);


        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(order1, order2);
        verify(orderRepository).findByStatus(OrderStatus.PENDING);
    }

    @Test
    void getRecentOrders_ReturnsOrdersInDateRange() {

        LocalDateTime sinceDate = LocalDateTime.now().minusDays(7);
        OrderEntity order1 = new OrderEntity();
        OrderEntity order2 = new OrderEntity();

        List<OrderEntity> recentOrders = Arrays.asList(order1, order2);
        when(orderRepository.findByOrderDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(recentOrders);


        List<OrderEntity> result = orderService.getRecentOrders(sinceDate);


        assertThat(result).hasSize(2);
        verify(orderRepository).findByOrderDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getOrdersWithTotalGreaterThan_ReturnsMatchingOrders() {

        BigDecimal minAmount = new BigDecimal("100.00");
        OrderEntity order1 = new OrderEntity();
        order1.setTotalAmount(new BigDecimal("150.00"));
        OrderEntity order2 = new OrderEntity();
        order2.setTotalAmount(new BigDecimal("200.00"));

        List<OrderEntity> orders = Arrays.asList(order1, order2);
        when(orderRepository.findOrdersWithTotalAmountGreaterThan(minAmount)).thenReturn(orders);


        List<OrderEntity> result = orderService.getOrdersWithTotalGreaterThan(minAmount);


        assertThat(result).hasSize(2);
        verify(orderRepository).findOrdersWithTotalAmountGreaterThan(minAmount);
    }

    @Test
    void updateOrderStatus_WhenOrderExists_UpdatesStatus() {

        OrderEntity existingOrder = new OrderEntity();
        existingOrder.setId(1L);
        existingOrder.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        OrderEntity updatedOrder = new OrderEntity();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.save(existingOrder)).thenReturn(updatedOrder);


        OrderEntity result = orderService.updateOrderStatus(1L, OrderStatus.DELIVERED);


        assertThat(existingOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(existingOrder);
    }

    @Test
    void updateOrderStatus_WhenOrderNotExists_ThrowsException() {

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> orderService.updateOrderStatus(999L, OrderStatus.DELIVERED))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: 999");

        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_WhenExists_DeletesSuccessfully() {

        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);


        assertThatCode(() -> orderService.deleteOrder(1L))
                .doesNotThrowAnyException();

        verify(orderRepository).existsById(1L);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_WhenNotExists_ThrowsException() {

        when(orderRepository.existsById(999L)).thenReturn(false);


        assertThatThrownBy(() -> orderService.deleteOrder(999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: 999");

        verify(orderRepository).existsById(999L);
        verify(orderRepository, never()).deleteById(any());
    }
}