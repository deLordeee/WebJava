package org.example.cosmocats.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cosmocats.common.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private List<Product> products;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
}
