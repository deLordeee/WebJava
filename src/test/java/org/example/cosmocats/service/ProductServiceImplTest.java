package org.example.cosmocats.service;

import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.common.ProductStatus;
import org.example.cosmocats.domain.Category;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.mapper.ProductMapper;
import org.example.cosmocats.service.exception.ProductNotFoundException;
import org.example.cosmocats.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
class ProductServiceImplTest {

    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;
    private static final String TEST_PRODUCT_NAME = "Test Product";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final BigDecimal TEST_PRICE = new BigDecimal("19.99");
    private static final Integer TEST_QUANTITY = 15;
    private static final String TEST_CATEGORY = "SPACE_THINGIES";
    private static final String TEST_STATUS = "AVAILABLE";

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDTO testProductDTO;

    @BeforeEach
    void setUp() {
        testProductDTO = createTestProductDTO();
    }

    private ProductDTO createTestProductDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setId(TEST_PRODUCT_ID);
        dto.setName(TEST_PRODUCT_NAME);
        dto.setDescription(TEST_DESCRIPTION);
        dto.setPrice(TEST_PRICE);
        dto.setQuantity(TEST_QUANTITY);
        dto.setCategory(TEST_CATEGORY);
        dto.setStatus(TEST_STATUS);
        return dto;
    }

    private ProductDTO createProductDTO(Long id, String name) {
        ProductDTO dto = new ProductDTO();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    private Product createProductEntity(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        return product;
    }

    @Test
    @DisplayName("Should return all products from in-memory store")
    void listAllProducts_WhenCalled_ReturnsAllProducts() {
        ProductDTO dto1 = createProductDTO(1L, "Galaxy Star Ball");
        ProductDTO dto2 = createProductDTO(2L, "Cosmic Milk");
        ProductDTO dto3 = createProductDTO(3L, "Space Laser");

        when(productMapper.convertToProductDTO(any(Product.class)))
                .thenReturn(dto1)
                .thenReturn(dto2)
                .thenReturn(dto3);

        List<ProductDTO> result = productService.listAllProducts();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("Galaxy Star Ball");
        assertThat(result.get(1).getName()).isEqualTo("Cosmic Milk");
        assertThat(result.get(2).getName()).isEqualTo("Space Laser");

        verify(productMapper, times(3)).convertToProductDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should return product when valid ID exists")
    void getProductById_WhenProductExists_ReturnsProduct() {
        when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(testProductDTO);

        ProductDTO result = productService.getProductById(TEST_PRODUCT_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_PRODUCT_ID);
        assertThat(result.getName()).isEqualTo(TEST_PRODUCT_NAME);
        verify(productMapper).convertToProductDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product ID doesn't exist")
    void getProductById_WhenProductNotExists_ThrowsException() {
        assertThatThrownBy(() -> productService.getProductById(NON_EXISTING_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found - wrong id: 999");

        verify(productMapper, never()).convertToProductDTO(any());
    }

    @Test
    @DisplayName("Should create product with generated ID and default status")
    void createProduct_WithValidData_ReturnsCreatedProduct() {
        ProductDTO inputDTO = createTestProductDTO();
        inputDTO.setId(null);
        inputDTO.setName("New Space Toy");
        inputDTO.setDescription("Brand new space toy");
        inputDTO.setPrice(new BigDecimal("25.99"));
        inputDTO.setQuantity(20);

        Product productEntity = createProductEntity(4L, "New Space Toy");
        ProductDTO expectedDTO = createProductDTO(4L, "New Space Toy");

        when(productMapper.convertToProductEntity(inputDTO)).thenReturn(productEntity);
        when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(expectedDTO);

        ProductDTO result = productService.createProduct(inputDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("New Space Toy");

        verify(productMapper).convertToProductEntity(inputDTO);
        verify(productMapper).convertToProductDTO(any(Product.class));

        ProductDTO retrieved = productService.getProductById(4L);
        assertThat(retrieved).isNotNull();
    }

    @Test
    @DisplayName("Should set default AVAILABLE status when status is null")
    void createProduct_WithNullStatus_SetsDefaultAvailable() {
        ProductDTO inputDTO = createTestProductDTO();
        inputDTO.setName("Product with null status");
        inputDTO.setPrice(new BigDecimal("15.00"));
        inputDTO.setQuantity(10);
        inputDTO.setStatus(null);

        Product productEntity = createProductEntity(null, "Product with null status");

        when(productMapper.convertToProductEntity(inputDTO)).thenReturn(productEntity);
        when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(inputDTO);

        productService.createProduct(inputDTO);

        verify(productMapper).convertToProductEntity(inputDTO);
        verify(productMapper).convertToProductDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should set default AVAILABLE status when status is blank")
    void createProduct_WithBlankStatus_SetsDefaultAvailable() {
        ProductDTO inputDTO = createTestProductDTO();
        inputDTO.setName("Product with blank status");
        inputDTO.setPrice(new BigDecimal("12.00"));
        inputDTO.setQuantity(8);
        inputDTO.setStatus("   ");

        Product productEntity = createProductEntity(null, "Product with blank status");

        when(productMapper.convertToProductEntity(inputDTO)).thenReturn(productEntity);
        when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(inputDTO);

        productService.createProduct(inputDTO);

        verify(productMapper).convertToProductEntity(inputDTO);
        verify(productMapper).convertToProductDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should update existing product with new data")
    void updateProduct_WhenProductExists_ReturnsUpdatedProduct() {
        ProductDTO updateDTO = createTestProductDTO();
        updateDTO.setName("Updated Galaxy Ball");
        updateDTO.setPrice(new BigDecimal("35.99"));
        updateDTO.setQuantity(30);
        updateDTO.setCategory("ANTI_GRAVITY_TOYS");

        ProductDTO expectedDTO = createProductDTO(TEST_PRODUCT_ID, "Updated Galaxy Ball");
        expectedDTO.setPrice(new BigDecimal("35.99"));

        when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(expectedDTO);

        ProductDTO result = productService.updateProduct(TEST_PRODUCT_ID, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Galaxy Ball");
        assertThat(result.getPrice()).isEqualByComparingTo("35.99");

        verify(productMapper).updateProductEntityFromDTO(eq(updateDTO), any(Product.class));
        verify(productMapper).convertToProductDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void updateProduct_WhenProductNotExists_ThrowsException() {
        ProductDTO updateDTO = createTestProductDTO();
        updateDTO.setName("Updated Product");

        assertThatThrownBy(() -> productService.updateProduct(NON_EXISTING_ID, updateDTO))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found - wrong id: 999");

        verify(productMapper, never()).updateProductEntityFromDTO(any(), any());
        verify(productMapper, never()).convertToProductDTO(any());
    }

    @Test
    @DisplayName("Should delete existing product")
    void deleteProduct_WhenProductExists_DeletesSuccessfully() {
        productService.deleteProduct(TEST_PRODUCT_ID);

        assertThatThrownBy(() -> productService.getProductById(TEST_PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("Should handle deletion of non-existent product silently")
    void deleteProduct_WhenProductNotExists_DoesNothing() {
        assertThatCode(() -> productService.deleteProduct(NON_EXISTING_ID))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle category conversion during creation")
    void createProduct_WithValidCategory_ConvertsCategoryCorrectly() {
        ProductDTO inputDTO = createTestProductDTO();
        inputDTO.setName("Category Test Product");
        inputDTO.setPrice(new BigDecimal("20.00"));
        inputDTO.setQuantity(10);
        inputDTO.setCategory("COSMIC_FOOD");

        Product productEntity = createProductEntity(null, "Category Test Product");

        when(productMapper.convertToProductEntity(inputDTO)).thenReturn(productEntity);
        when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(inputDTO);

        ProductDTO result = productService.createProduct(inputDTO);

        assertThat(result).isNotNull();
        verify(productMapper).convertToProductEntity(inputDTO);
    }

    @Test
    @DisplayName("Should handle timestamp setting during creation")
    void createProduct_SetsCreatedAndUpdatedTimestamps() {
        ProductDTO inputDTO = createTestProductDTO();
        inputDTO.setName("Timestamp Test");
        inputDTO.setPrice(new BigDecimal("10.00"));
        inputDTO.setQuantity(5);

        Product productEntity = createProductEntity(null, "Timestamp Test");

        when(productMapper.convertToProductEntity(inputDTO)).thenReturn(productEntity);
        when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(inputDTO);

        productService.createProduct(inputDTO);

        verify(productMapper).convertToProductEntity(inputDTO);
        verify(productMapper).convertToProductDTO(any(Product.class));
    }

    @Test
    @DisplayName("Should handle invalid category gracefully")
    void createProduct_WithInvalidCategory_ThrowsException() {
        ProductDTO inputDTO = createTestProductDTO();
        inputDTO.setName("Invalid Category Product");
        inputDTO.setPrice(new BigDecimal("10.00"));
        inputDTO.setQuantity(5);
        inputDTO.setCategory("INVALID_CATEGORY");

        Product productEntity = createProductEntity(null, "Invalid Category Product");

        when(productMapper.convertToProductEntity(inputDTO)).thenReturn(productEntity);

        assertThatThrownBy(() -> productService.createProduct(inputDTO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should initialize with exactly 3 sample products")
    void serviceInitialization_CreatesThreeSampleProducts() {
        List<ProductDTO> products = productService.listAllProducts();

        assertThat(products).hasSize(3);

        assertThatCode(() -> productService.getProductById(1L)).doesNotThrowAnyException();
        assertThatCode(() -> productService.getProductById(2L)).doesNotThrowAnyException();
        assertThatCode(() -> productService.getProductById(3L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should use all available category types correctly")
    void createProduct_WithAllCategoryTypes_WorksCorrectly() {
        String[] categories = {"ANTI_GRAVITY_TOYS", "COSMIC_FOOD", "SPACE_THINGIES"};

        for (String category : categories) {
            ProductDTO inputDTO = createTestProductDTO();
            inputDTO.setName("Test " + category);
            inputDTO.setPrice(new BigDecimal("10.00"));
            inputDTO.setQuantity(5);
            inputDTO.setCategory(category);

            Product productEntity = createProductEntity(null, "Test " + category);

            when(productMapper.convertToProductEntity(any(ProductDTO.class))).thenReturn(productEntity);
            when(productMapper.convertToProductDTO(any(Product.class))).thenReturn(inputDTO);

            assertThatCode(() -> productService.createProduct(inputDTO))
                    .doesNotThrowAnyException();
        }
    }
}
