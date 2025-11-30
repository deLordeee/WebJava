package org.example.cosmocats.web;

import org.example.cosmocats.AbstractIT;
import org.example.cosmocats.dto.ProductDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.testcontainers.DockerClientFactory;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Product Controller Integration Tests")
@Tag("integration")
class ProductControllerIT extends AbstractIT {

    @Test
    @DisplayName("Should return all products with pagination support")
    void getAllProducts_ReturnsInitializedProducts() throws Exception {
        mockMvc.perform(get("/v1/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));

    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void getProductById_Success() throws Exception {
        mockMvc.perform(get("/v1/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void getProductById_NotFound() throws Exception {
        mockMvc.perform(get("/v1/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create valid product successfully")
    void createProduct_Valid_Success() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Galaxy Toy");
        dto.setDescription("Amazing galaxy toy for cosmic cats");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");
        dto.setStatus("AVAILABLE");

        mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Galaxy Toy"));
    }

    @Test
    @DisplayName("Should reject product with invalid name")
    void createProduct_InvalidName_BadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Bad");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");

        mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject product with negative price")
    void createProduct_NegativePrice_BadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Toy");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("-5.00"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");

        mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject product with invalid category")
    void createProduct_InvalidCategory_BadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Toy");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("INVALID");

        mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update existing product successfully")
    void updateProduct_Success() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Galaxy Ball Updated");
        dto.setDescription("Updated description");
        dto.setPrice(new BigDecimal("35.99"));
        dto.setQuantity(40);
        dto.setCategory("ANTI_GRAVITY_TOYS");
        dto.setStatus("AVAILABLE");

        mockMvc.perform(put("/v1/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Galaxy Ball Updated"));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent product")
    void updateProduct_NotFound() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Toy");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");

        mockMvc.perform(put("/v1/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete product successfully")
    void deleteProduct_Success() throws Exception {

        ProductDTO dto = new ProductDTO();
        dto.setName("Galaxy Toy To Delete");
        dto.setDescription("Temporary product for deletion test");
        dto.setPrice(new BigDecimal("19.99"));
        dto.setQuantity(10);
        dto.setCategory("ANTI_GRAVITY_TOYS");
        dto.setStatus("AVAILABLE");

        String response = mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();


        Integer createdId = Math.toIntExact(objectMapper.readValue(response, ProductDTO.class).getId());

        mockMvc.perform(delete("/v1/api/products/" + createdId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should verify full CRUD flow")
    void createAndDelete_FullFlow() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Product");
        dto.setDescription("Temporary product for testing");
        dto.setPrice(new BigDecimal("10.00"));
        dto.setQuantity(1);
        dto.setCategory("SPACE_THINGIES");

        String response = mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductDTO created = objectMapper.readValue(response, ProductDTO.class);

        mockMvc.perform(delete("/v1/api/products/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/api/products/" + created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should reject product with missing required fields")
    void createProduct_MissingFields_BadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Product");

        mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle zero quantity product creation")
    void createProduct_ZeroQuantity_Success() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Cosmic Zero Stock Item"); // ← Add cosmic term
        dto.setDescription("Item with no stock");
        dto.setPrice(new BigDecimal("15.99"));
        dto.setQuantity(0);
        dto.setCategory("SPACE_THINGIES");
        dto.setStatus("OUT_OF_STOCK");

        mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(0));
    }

    @Test
    @DisplayName("Should return empty list when no products match criteria")
    void getProducts_EmptyResult() throws Exception {

        String response = mockMvc.perform(get("/v1/api/products"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<ProductDTO> products = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ProductDTO.class));


        for (ProductDTO product : products) {
            mockMvc.perform(delete("/v1/api/products/" + product.getId()))
                    .andExpect(status().isNoContent());
        }


        mockMvc.perform(get("/v1/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}