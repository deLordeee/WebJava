package org.example.cosmocats.service.impl;





import org.example.cosmocats.domain.Product;
import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.mapper.ProductMapper;
import org.example.cosmocats.service.exception.ProductNotFoundException;
import org.example.cosmocats.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void listAllProducts_ReturnsProducts() {
        when(productMapper.convertToProductDTO(any())).thenReturn(new ProductDTO());

        List<ProductDTO> result = productService.listAllProducts();

        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        verify(productMapper, atLeast(3)).convertToProductDTO(any());
    }

    @Test
    void getProductById_WhenExists_ReturnsProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        dto.setName("Galaxy Star Ball");
        when(productMapper.convertToProductDTO(any())).thenReturn(dto);

        ProductDTO result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getProductById_WhenNotExists_ThrowsException() {
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found - wrong id: 999");
    }

    @Test
    void createProduct_Success() {
        ProductDTO input = new ProductDTO();
        input.setName("Star Toy");
        input.setPrice(new BigDecimal("25.00"));
        input.setQuantity(10);
        input.setCategory("ANTI_GRAVITY_TOYS");
        input.setStatus("AVAILABLE");

        ProductDTO output = new ProductDTO();
        output.setId(4L);

        when(productMapper.convertToProductEntity(any(ProductDTO.class))).thenReturn(new Product());
        when(productMapper.convertToProductDTO(any())).thenReturn(output);

        ProductDTO result = productService.createProduct(input);

        assertThat(result.getId()).isNotNull();
        verify(productMapper).convertToProductEntity(any(ProductDTO.class));
        verify(productMapper).convertToProductDTO(any());
    }

    @Test
    void createProduct_WithNullStatus_SetsDefaultAvailable() {
        ProductDTO input = new ProductDTO();
        input.setName("Star Toy");
        input.setPrice(new BigDecimal("25.00"));
        input.setQuantity(10);
        input.setCategory("ANTI_GRAVITY_TOYS");
        input.setStatus(null);

        when(productMapper.convertToProductEntity(any())).thenReturn(new Product());
        when(productMapper.convertToProductDTO(any())).thenReturn(new ProductDTO());

        productService.createProduct(input);

        verify(productMapper).convertToProductEntity(any());
    }



    @Test
    void updateProduct_WhenExists_Success() {
        ProductDTO update = new ProductDTO();
        update.setName("Updated");
        update.setPrice(new BigDecimal("35.00"));
        update.setQuantity(25);
        update.setCategory("COSMIC_FOOD");

        when(productMapper.convertToProductDTO(any())).thenReturn(update);

        ProductDTO result = productService.updateProduct(1L, update);

        assertThat(result).isNotNull();
        verify(productMapper).updateProductEntityFromDTO(any(), any());
    }

    @Test
    void updateProduct_WhenNotExists_ThrowsException() {
        ProductDTO update = new ProductDTO();
        update.setName("Updated");

        assertThatThrownBy(() -> productService.updateProduct(999L, update))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found - wrong id: 999");
    }

    @Test
    void deleteProduct_Success() {
        assertThatCode(() -> productService.deleteProduct(1L))
                .doesNotThrowAnyException();
    }
}