package org.example.cosmocats.repository;

import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.common.ProductStatus;
import org.example.cosmocats.repository.projection.PopularProductReport;
import org.example.cosmocats.repository.projection.ProductSalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Basic queries using method names
    List<ProductEntity> findByCategoryId(Long categoryId);
    List<ProductEntity> findByStatus(ProductStatus status);
    List<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    boolean existsByNameAndCategoryId(String name, Long categoryId);


    @Query("SELECT p FROM ProductEntity p WHERE p.category.type = :categoryType AND p.status = 'AVAILABLE'")
    List<ProductEntity> findAvailableProductsByCategoryType(@Param("categoryType") String categoryType);

    @Query("SELECT p FROM ProductEntity p WHERE p.price > :minPrice ORDER BY p.price DESC")
    List<ProductEntity> findProductsMoreExpensiveThan(@Param("minPrice") BigDecimal minPrice);

    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.category.id = :categoryId")
    Long countByCategory(@Param("categoryId") Long categoryId);


    @Query("SELECT NEW org.example.cosmocats.repository.projection.ProductSalesReport(" +
            "p.name, SUM(oi.quantity), SUM(CAST(oi.quantity * oi.priceAtOrder AS BigDecimal))) " +
            "FROM OrderItemEntity oi JOIN oi.product p " +
            "GROUP BY p.id, p.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<ProductSalesReport> findProductSalesReport();

    @Query("SELECT NEW org.example.cosmocats.repository.projection.PopularProductReport(" +
            "p.name, COUNT(oi.id), p.category.type) " +
            "FROM ProductEntity p LEFT JOIN p.orderItems oi " +
            "GROUP BY p.id, p.name, p.category.type " +
            "HAVING COUNT(oi.id) > 0 " +
            "ORDER BY COUNT(oi.id) DESC")
    List<PopularProductReport> findPopularProducts();



    @Query("SELECT p FROM ProductEntity p WHERE p.quantity < :threshold AND p.status = 'AVAILABLE' " +
            "ORDER BY p.quantity ASC")
    List<ProductEntity> findLowStockProducts(@Param("threshold") Integer threshold);
}