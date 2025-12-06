package org.example.cosmocats.service;

import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.repository.projection.PopularProductReport;
import org.example.cosmocats.repository.projection.ProductSalesReport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

    List<ProductDTO> listAllProducts();

    ProductDTO getProductById(Long productId);

    ProductDTO createProduct(ProductDTO productRequestDTO);

    ProductDTO updateProduct(Long productId, ProductDTO productRequestDTO);

    void deleteProduct(Long productId);

    // Report methods
    @Transactional(readOnly = true)
    List<ProductSalesReport> getProductSalesReport();

    @Transactional(readOnly = true)
    List<PopularProductReport> getPopularProducts();

    @Transactional(readOnly = true)
    List<ProductEntity> getLowStockProducts(Integer threshold);

    @Transactional(readOnly = true)
    List<ProductEntity> getAvailableProductsByCategory(String categoryType);
}
