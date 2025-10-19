package org.example.cosmocats.service.impl;

import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.common.ProductStatus;
import org.example.cosmocats.domain.Category;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.mapper.ProductMapper;
import org.example.cosmocats.service.ProductService;
import org.example.cosmocats.service.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductServiceImpl implements ProductService {

    private final Map<Long, Product> inMemoryProductStore = new HashMap<>();
    private final AtomicLong productIdGenerator = new AtomicLong(1);
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
        initializeSampleProducts();
    }

    private void initializeSampleProducts() {
        addSampleProduct("Galaxy Star Ball",
                "Anti-gravity toy that floats in the galaxy",
                new BigDecimal("29.99"), 50, CategoryType.ANTI_GRAVITY_TOYS);

        addSampleProduct("Cosmic Milk",
                "Tasty milk from intergalactic store",
                new BigDecimal("15.50"), 100, CategoryType.COSMIC_FOOD);

        addSampleProduct("Space Laser",
                "Laser for intergalactic cat entertainment",
                new BigDecimal("12.75"), 25, CategoryType.SPACE_THINGIES);
    }

    private void addSampleProduct(String name, String description,
                                  BigDecimal price, Integer quantity, CategoryType categoryType) {
        Category category = new Category();
        category.setId(1L);
        category.setType(categoryType);
        category.setDescription(categoryType.name());

        Product product = new Product();
        product.setId(productIdGenerator.getAndIncrement());
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        product.setStatus(ProductStatus.AVAILABLE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        inMemoryProductStore.put(product.getId(), product);
    }

    @Override
    public List<ProductDTO> listAllProducts() {
        return inMemoryProductStore.values().stream()
                .map(productMapper::convertToProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = inMemoryProductStore.get(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product not found - wrong id: " + productId);
        }
        return productMapper.convertToProductDTO(product);
    }

    @Override
    public ProductDTO createProduct(ProductDTO productRequestDTO) {
        Product product = productMapper.convertToProductEntity(productRequestDTO);

        Long newId = productIdGenerator.getAndIncrement();
        product.setId(newId);

        Category category = new Category();
        category.setType(CategoryType.valueOf(productRequestDTO.getCategory()));
        category.setDescription(productRequestDTO.getCategory());
        product.setCategory(category);

        if (productRequestDTO.getStatus() == null || productRequestDTO.getStatus().isBlank()) {
            product.setStatus(ProductStatus.AVAILABLE);
        }

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        inMemoryProductStore.put(newId, product);
        return productMapper.convertToProductDTO(product);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productRequestDTO) {
        Product productToUpdate = inMemoryProductStore.get(productId);
        if (productToUpdate == null) {
            throw new ProductNotFoundException("Product not found - wrong id: " + productId);
        }

        productMapper.updateProductEntityFromDTO(productRequestDTO, productToUpdate);

        if (productRequestDTO.getCategory() != null) {
            Category category = new Category();
            category.setType(CategoryType.valueOf(productRequestDTO.getCategory()));
            category.setDescription(productRequestDTO.getCategory());
            productToUpdate.setCategory(category);
        }

        productToUpdate.setUpdatedAt(LocalDateTime.now());
        inMemoryProductStore.put(productId, productToUpdate);
        return productMapper.convertToProductDTO(productToUpdate);
    }

    @Override
    public void deleteProduct(Long productId) {
        inMemoryProductStore.remove(productId);
    }
}
