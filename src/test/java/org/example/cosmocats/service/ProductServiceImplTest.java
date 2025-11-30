package org.example.cosmocats.service;





import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.common.ProductStatus;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.mapper.ProductEntityMapper;
import org.example.cosmocats.mapper.ProductMapper;
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
    void listAllProducts_ReturnsProducts() {
        // Given
        ProductEntity product1 = new ProductEntity();
        ProductEntity product2 = new ProductEntity();
        ProductEntity product3 = new ProductEntity();

        List<ProductEntity> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);
        when(productEntityMapper.convertToProductDTO(any(ProductEntity.class))).thenReturn(new ProductDTO());

        // When
        List<ProductDTO> result = productService.listAllProducts();

        // Then
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        verify(productRepository).findAll();
        verify(productEntityMapper, times(3)).convertToProductDTO(any(ProductEntity.class));
    }

    @Test
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
    void createProduct_WithNullStatus_SetsDefaultAvailable() {

        ProductDTO input = new ProductDTO();
        input.setName("Star Toy");
        input.setPrice(new BigDecimal("25.00"));
        input.setQuantity(10);
        input.setCategory("ANTI_GRAVITY_TOYS");
        input.setStatus(null);

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setType(CategoryType.ANTI_GRAVITY_TOYS);
        when(categoryRepository.findByType(CategoryType.ANTI_GRAVITY_TOYS))
                .thenReturn(Optional.of(category));

        when(productRepository.existsByNameAndCategoryId(anyString(), anyLong()))
                .thenReturn(false);

        ProductEntity productEntity = new ProductEntity();
        when(productEntityMapper.convertToProductEntity(input)).thenReturn(productEntity);

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setStatus(ProductStatus.AVAILABLE);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedProduct);

        when(productEntityMapper.convertToProductDTO(any(ProductEntity.class)))
                .thenReturn(new ProductDTO());


        productService.createProduct(input);


        verify(productEntityMapper).convertToProductEntity(input);
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_WhenExists_Success() {

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        ProductDTO update = new ProductDTO();
        update.setName("Updated");
        update.setPrice(new BigDecimal("35.00"));
        update.setQuantity(25);
        update.setCategory("COSMIC_FOOD");

        CategoryEntity category = new CategoryEntity();
        category.setType(CategoryType.COSMIC_FOOD);
        when(categoryRepository.findByType(CategoryType.COSMIC_FOOD))
                .thenReturn(Optional.of(category));

        ProductEntity updatedProduct = new ProductEntity();
        when(productRepository.save(any(ProductEntity.class))).thenReturn(updatedProduct);

        ProductDTO resultDTO = new ProductDTO();
        when(productEntityMapper.convertToProductDTO(updatedProduct)).thenReturn(resultDTO);


        ProductDTO result = productService.updateProduct(1L, update);


        assertThat(result).isNotNull();
        verify(productRepository).findById(1L);
        verify(productEntityMapper).updateProductEntityFromDTO(update, existingProduct);
        verify(categoryRepository).findByType(CategoryType.COSMIC_FOOD);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateProduct_WhenNotExists_ThrowsException() {

        ProductDTO update = new ProductDTO();
        update.setName("Updated");
        when(productRepository.findById(999L)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> productService.updateProduct(999L, update))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found - wrong id: 999");

        verify(productRepository).findById(999L);
    }

    @Test
    void deleteProduct_Success() {

        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);


        assertThatCode(() -> productService.deleteProduct(1L))
                .doesNotThrowAnyException();

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }
}