package org.example.cosmocats.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cosmocats.common.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private List<Product> products;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
}
