package org.example.cosmocats.web;





import org.example.cosmocats.AbstractIT;
import org.example.cosmocats.dto.ProductDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@DisplayName("Product Controller IT")
@Tag("controller")
class ProductControllerIT extends AbstractIT {

    @Test
    void getAllProducts_ReturnsInitializedProducts() throws Exception {
        mockMvc.perform(get("/v1/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void getProductById_Success() throws Exception {
        mockMvc.perform(get("/v1/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void getProductById_NotFound() throws Exception {
        mockMvc.perform(get("/v1/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_Valid_Success() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Galaxy Toy");
        dto.setDescription("Amazing galaxy toy for cosmic cats");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");
        dto.setStatus("AVAILABLE");

        mockMvc.perform(post("/v1/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Galaxy Toy"));
    }

    @Test
    void createProduct_InvalidName_BadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Bad");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");

        mockMvc.perform(post("/v1/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_NegativePrice_BadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Toy");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("-5.00"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");

        mockMvc.perform(post("/v1/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_InvalidCategory_BadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Toy");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("INVALID");

        mockMvc.perform(post("/v1/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_Success() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Galaxy Ball Updated");
        dto.setDescription("Updated description");
        dto.setPrice(new BigDecimal("35.99"));
        dto.setQuantity(40);
        dto.setCategory("ANTI_GRAVITY_TOYS");
        dto.setStatus("AVAILABLE");

        mockMvc.perform(put("/v1/api/products/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Galaxy Ball Updated"));
    }

    @Test
    void updateProduct_NotFound() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Toy");
        dto.setDescription("Valid description");
        dto.setPrice(new BigDecimal("25.99"));
        dto.setQuantity(100);
        dto.setCategory("ANTI_GRAVITY_TOYS");

        mockMvc.perform(put("/v1/api/products/999")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/v1/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createAndDelete_FullFlow() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Product");
        dto.setDescription("Temporary product for testing");
        dto.setPrice(new BigDecimal("10.00"));
        dto.setQuantity(1);
        dto.setCategory("SPACE_THINGIES");

        String response = mockMvc.perform(post("/v1/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProductDTO created = objectMapper.readValue(response, ProductDTO.class);

        mockMvc.perform(delete("/v1/api/products/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/api/products/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}