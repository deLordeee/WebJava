package org.example.cosmocats.service;

import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.common.ProductStatus;
import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.mapper.ProductEntityMapper;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.service.exception.ProductNotFoundException;
import org.example.cosmocats.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @WithMockUser(roles = {"USER"})
    void listAllProducts_ReturnsProducts() {
        // Given
        ProductEntity product1 = new ProductEntity();
        ProductEntity product2 = new ProductEntity();
        ProductEntity product3 = new ProductEntity();

        List<ProductEntity> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);
        when(productEntityMapper.convertToProductDTO(any(ProductEntity.class)))
                .thenReturn(new ProductDTO());

        // When
        List<ProductDTO> result = productService.listAllProducts();

        // Then
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        verify(productRepository).findAll();
        verify(productEntityMapper, times(3)).convertToProductDTO(any(ProductEntity.class));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getProductById_WhenExists_ReturnsProduct() {
        // Given
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        dto.setName("Galaxy Star Ball");
        when(productEntityMapper.convertToProductDTO(product)).thenReturn(dto);

        // When
        ProductDTO result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).findById(1L);
        verify(productEntityMapper).convertToProductDTO(product);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getProductById_WhenNotExists_ThrowsException() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found - wrong id: 999");

        verify(productRepository).findById(999L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createProduct_Success() {
        // Given
        ProductDTO input = new ProductDTO();
        input.setName("Star Toy");
        input.setPrice(new BigDecimal("25.00"));
        input.setQuantity(10);
        input.setCategory("ANTI_GRAVITY_TOYS");
        input.setStatus("AVAILABLE");

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setType(CategoryType.ANTI_GRAVITY_TOYS);
        when(categoryRepository.findByType(CategoryType.ANTI_GRAVITY_TOYS))
                .thenReturn(Optional.of(category));

        when(productRepository.existsByNameAndCategoryId(input.getName(), category.getId()))
                .thenReturn(false);

        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(input.getName());
        when(productEntityMapper.convertToProductEntity(input)).thenReturn(productEntity);

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setId(4L);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedProduct);

        ProductDTO output = new ProductDTO();
        output.setId(4L);
        when(productEntityMapper.convertToProductDTO(savedProduct)).thenReturn(output);

        // When
        ProductDTO result = productService.createProduct(input);

        // Then
        assertThat(result.getId()).isNotNull();
        verify(categoryRepository).findByType(CategoryType.ANTI_GRAVITY_TOYS);
        verify(productRepository).existsByNameAndCategoryId(input.getName(), category.getId());
        verify(productEntityMapper).convertToProductEntity(input);
        verify(productRepository).save(any(ProductEntity.class));
        verify(productEntityMapper).convertToProductDTO(savedProduct);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteProduct_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // When/Then
        assertThatCode(() -> productService.deleteProduct(1L))
                .doesNotThrowAnyException();

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }
}