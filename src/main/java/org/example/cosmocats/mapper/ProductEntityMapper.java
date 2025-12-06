package org.example.cosmocats.mapper;

import org.example.cosmocats.dto.ProductDTO;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.common.ProductStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    ProductEntityMapper INSTANCE = Mappers.getMapper(ProductEntityMapper.class);

    // Convert Entity to DTO
    @Mapping(source = "category.type", target = "category")
    @Mapping(source = "status", target = "status")
    ProductDTO convertToProductDTO(ProductEntity productEntity);

    // Convert DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true) // We'll set this manually
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    ProductEntity convertToProductEntity(ProductDTO productDTO);

    // Update Entity from DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true) // We'll set this manually
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "orderItems", ignore = true)
    void updateProductEntityFromDTO(ProductDTO productDTO, @MappingTarget ProductEntity productEntity);

    // Helper method to convert category string to CategoryEntity
    @Named("stringToCategoryEntity")
    default CategoryEntity stringToCategoryEntity(String categoryType) {
        if (categoryType == null) {
            return null;
        }
        CategoryEntity category = new CategoryEntity();
        category.setType(CategoryType.valueOf(categoryType));
        return category;
    }

    // Helper method to set timestamps
    default ProductEntity setTimestamps(ProductEntity entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}