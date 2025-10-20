package org.example.cosmocats.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long id;
    private Map<Product, Integer> items = new HashMap<>();
    private BigDecimal totalPrice;
}
