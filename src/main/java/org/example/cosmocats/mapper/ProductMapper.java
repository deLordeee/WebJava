package org.example.cosmocats.mapper;

import org.example.cosmocats.common.ProductStatus;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.dto.ProductDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class}
)
public interface ProductMapper {

  
    @Mapping(source = "category.type", target = "category")
    ProductDTO convertToProductDTO(Product productEntity);

 
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product convertToProductEntity(ProductDTO productDTO);

   
    List<ProductDTO> convertToProductDTOList(List<Product> productEntities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProductEntityFromDTO(ProductDTO updatedProductData, @MappingTarget Product existingProduct);

  
    @Named("mapStatusEnumToString")
    default String mapStatusEnumToString(ProductStatus productStatus) {
        return productStatus != null ? productStatus.name() : null;
    }

    
    @Named("mapStringToStatusEnum")
    default ProductStatus mapStringToStatusEnum(String statusValue) {
        if (statusValue == null || statusValue.isBlank()) {
            return null;
        }
        return ProductStatus.valueOf(statusValue.trim().toUpperCase());
    }
}
