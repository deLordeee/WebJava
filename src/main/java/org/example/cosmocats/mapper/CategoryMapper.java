package org.example.cosmocats.mapper;

import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.domain.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {

    default String categoryToString(Category category) {
        return category != null && category.getType() != null
                ? category.getType().name()
                : null;
    }

    default Category stringToCategory(String categoryType) {
        if (categoryType == null) {
            return null;
        }
        Category category = new Category();
        category.setType(CategoryType.valueOf(categoryType));
        return category;
    }
}