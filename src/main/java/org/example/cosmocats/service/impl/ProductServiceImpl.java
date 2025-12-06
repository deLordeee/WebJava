package org.example.cosmocats.service.impl;

import jakarta.annotation.PostConstruct;
import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.common.ProductStatus;
import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.repository.projection.ProductSalesReport;
import org.example.cosmocats.repository.projection.PopularProductReport;

import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.mapper.ProductEntityMapper;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.service.ProductService;
import org.example.cosmocats.service.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductEntityMapper productEntityMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productEntityMapper = productEntityMapper;

    }
    @PostConstruct
    public void init() {
        initializeSampleData();
    }
    private void initializeSampleData() {

        for (CategoryType type : CategoryType.values()) {
            if (categoryRepository.findByType(type).isEmpty()) {
                CategoryEntity category = new CategoryEntity();
                category.setType(type);
                category.setDescription(type.name() + " - Intergalactic category");
                categoryRepository.save(category);
            }
        }


        if (productRepository.count() == 0) {
            createSampleProduct("Galaxy Star Ball",
                    "Anti-gravity toy that floats in the galaxy",
                    new BigDecimal("29.99"), 50, CategoryType.ANTI_GRAVITY_TOYS);

            createSampleProduct("Cosmic Milk",
                    "Tasty milk from intergalactic store",
                    new BigDecimal("15.50"), 100, CategoryType.COSMIC_FOOD);

            createSampleProduct("Space Laser",
                    "Laser for intergalactic cat entertainment",
                    new BigDecimal("12.75"), 25, CategoryType.SPACE_THINGIES);
        }
    }

    private void createSampleProduct(String name, String description,
                                     BigDecimal price, Integer quantity, CategoryType categoryType) {
        CategoryEntity category = categoryRepository.findByType(categoryType)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryType));

        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        product.setStatus(ProductStatus.AVAILABLE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> listAllProducts() {
        return productRepository.findAll().stream()
                .map(productEntityMapper::convertToProductDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found - wrong id: " + productId));
        return productEntityMapper.convertToProductDTO(product);
    }

    @Override
    public ProductDTO createProduct(ProductDTO productRequestDTO) {

        CategoryEntity category = categoryRepository.findByType(
                        CategoryType.valueOf(productRequestDTO.getCategory()))
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (productRepository.existsByNameAndCategoryId(productRequestDTO.getName(), category.getId())) {
            throw new RuntimeException("Product with name '" + productRequestDTO.getName() +
                    "' already exists in category " + productRequestDTO.getCategory());
        }

        ProductEntity product = productEntityMapper.convertToProductEntity(productRequestDTO);
        product.setCategory(category);

        if (productRequestDTO.getStatus() == null || productRequestDTO.getStatus().isBlank()) {
            product.setStatus(ProductStatus.AVAILABLE);
        } else {
            product.setStatus(ProductStatus.valueOf(productRequestDTO.getStatus()));
        }

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        ProductEntity savedProduct = productRepository.save(product);
        return productEntityMapper.convertToProductDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productRequestDTO) {
        ProductEntity productToUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found - wrong id: " + productId));

        productEntityMapper.updateProductEntityFromDTO(productRequestDTO, productToUpdate);

        if (productRequestDTO.getCategory() != null) {
            CategoryEntity category = categoryRepository.findByType(
                            CategoryType.valueOf(productRequestDTO.getCategory()))
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            productToUpdate.setCategory(category);
        }

        productToUpdate.setUpdatedAt(LocalDateTime.now());
        ProductEntity updatedProduct = productRepository.save(productToUpdate);

        return productEntityMapper.convertToProductDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found - wrong id: " + productId);
        }
        productRepository.deleteById(productId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProductSalesReport> getProductSalesReport() {
        return productRepository.findProductSalesReport();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PopularProductReport> getPopularProducts() {
        return productRepository.findPopularProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> getAvailableProductsByCategory(String categoryType) {
        return productRepository.findAvailableProductsByCategoryType(categoryType);
    }
}