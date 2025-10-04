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
    ProductDTO toDTO(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductDTO productDTO);

    List<ProductDTO> toDTOList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product entity);


    @Named("statusToString")
    default String statusToString(ProductStatus status) {
        return status != null ? status.name() : null;
    }


    @Named("stringToStatus")
    default ProductStatus stringToStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return ProductStatus.valueOf(status.trim().toUpperCase());
    }
}