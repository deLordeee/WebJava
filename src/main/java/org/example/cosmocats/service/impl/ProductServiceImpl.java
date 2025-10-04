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

    private final Map<Long, Product> products = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
        initializeMockData();
    }

    private void initializeMockData() {
        createMockData("Galaxy Star Ball",
                "Anti-gravity toy that floats in the galaxy",
                new BigDecimal("29.99"), 50, CategoryType.ANTI_GRAVITY_TOYS);

        createMockData("Cosmic Milk",
                "Tasty milk from intergalactic store",
                new BigDecimal("15.50"), 100, CategoryType.COSMIC_FOOD);

        createMockData("Space Laser ",
                "Laser for intergalactic cat entertainment",
                new BigDecimal("12.75"), 25, CategoryType.SPACE_THINGIES);
    }

    private void createMockData(String name, String description, BigDecimal price,
                                   Integer quantity, CategoryType categoryType) {
        Category category = new Category();
        category.setId(1L);
        category.setType(categoryType);
        category.setDescription(categoryType.name());

        Product product = new Product();
        product.setId(idCounter.getAndIncrement());
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        product.setStatus(ProductStatus.AVAILABLE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        products.put(product.getId(), product);
    }

    @Override
    public List<ProductDTO> findAll() {
        return products.values().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = products.get(id);
        if (product == null) {
            throw new ProductNotFoundException("Product not found  - wrong  id: " + id);
        }
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO create(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);

        Long newId = idCounter.getAndIncrement();
        product.setId(newId);


        Category category = new Category();
        category.setType(CategoryType.valueOf(productDTO.getCategory()));
        category.setDescription(productDTO.getCategory());
        product.setCategory(category);


        if (productDTO.getStatus() == null || productDTO.getStatus().isBlank()) {
            product.setStatus(ProductStatus.AVAILABLE);
        }

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        products.put(newId, product);
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO productDTO) {
        Product existingProduct = products.get(id);
        if (existingProduct == null) {
            throw new ProductNotFoundException("Product not found  - wrong  id: " + id);
        }


        productMapper.updateEntityFromDTO(productDTO, existingProduct);


        if (productDTO.getCategory() != null) {
            Category category = new Category();
            category.setType(CategoryType.valueOf(productDTO.getCategory()));
            category.setDescription(productDTO.getCategory());
            existingProduct.setCategory(category);
        }

        existingProduct.setUpdatedAt(LocalDateTime.now());

        products.put(id, existingProduct);
        return productMapper.toDTO(existingProduct);
    }

    @Override
    public void delete(Long id) {
        if (!products.containsKey(id)) {
            throw new ProductNotFoundException("Product not found  - wrong  id: " + id);
        }
        products.remove(id);
    }
}