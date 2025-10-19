package org.example.cosmocats.service;

import org.example.cosmocats.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> listAllProducts();

    ProductDTO getProductById(Long productId);

    ProductDTO createProduct(ProductDTO productRequestDTO);

    ProductDTO updateProduct(Long productId, ProductDTO productRequestDTO);

    void deleteProduct(Long productId);
}
