package org.example.cosmocats.service.impl;

import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.service.CategoryService;
import org.example.cosmocats.service.exception.CategoryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {

        if (categoryRepository.findByType(category.getType()).isPresent()) {
            throw new RuntimeException("Category with type " + category.getType() + " already exists");
        }

        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryEntity> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryEntity> getCategoryByType(CategoryType type) {
        return categoryRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryEntity> searchCategoriesByDescription(String keyword) {
        return categoryRepository.findByDescriptionContaining(keyword);
    }

    @Override
    public CategoryEntity updateCategory(Long categoryId, CategoryEntity category) {
        CategoryEntity existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));


        Optional<CategoryEntity> categoryWithSameType = categoryRepository.findByType(category.getType());
        if (categoryWithSameType.isPresent() && !categoryWithSameType.get().getId().equals(categoryId)) {
            throw new RuntimeException("Another category with type " + category.getType() + " already exists");
        }

        existingCategory.setType(category.getType());
        existingCategory.setDescription(category.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category not found with id: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }
}