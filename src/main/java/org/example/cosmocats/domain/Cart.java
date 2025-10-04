package org.example.cosmocats.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long id;
    private Map<Product, Integer> items = new HashMap<>();
    private BigDecimal totalPrice;
}
