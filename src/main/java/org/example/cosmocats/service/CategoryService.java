package org.example.cosmocats.service;

import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.common.CategoryType;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryEntity createCategory(CategoryEntity category);

    List<CategoryEntity> getAllCategories();

    Optional<CategoryEntity> getCategoryById(Long id);

    Optional<CategoryEntity> getCategoryByType(CategoryType type);

    List<CategoryEntity> searchCategoriesByDescription(String keyword);

    CategoryEntity updateCategory(Long categoryId, CategoryEntity category);

    void deleteCategory(Long categoryId);
}