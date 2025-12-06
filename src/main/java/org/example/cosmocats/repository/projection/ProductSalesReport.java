package org.example.cosmocats.repository.projection;

import java.math.BigDecimal;

public record ProductSalesReport(
        String productName,
        Long totalQuantity,
        BigDecimal totalRevenue
) {
    public String getSalesSummary() {
        return String.format("Product: %s, Quantity sold: %d, Revenue: %s",
                productName(), totalQuantity(), totalRevenue());
    }
}