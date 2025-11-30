package org.example.cosmocats.repository.projection;



import org.example.cosmocats.common.CategoryType;

public record PopularProductReport(
        String name,
        Long orderCount,
        CategoryType categoryType
) {
    public String getPopularitySummary() {
        return String.format("Product: %s, Orders: %d, Category: %s",
                name(), orderCount(), categoryType());
    }
}