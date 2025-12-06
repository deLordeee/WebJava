package org.example.cosmocats.service;

import jakarta.persistence.PersistenceException;
import org.example.cosmocats.common.CategoryType;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.service.exception.CategoryNotFoundException;
import org.example.cosmocats.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_WithUniqueType_Success() {

        CategoryEntity category = new CategoryEntity();
        category.setType(CategoryType.ANTI_GRAVITY_TOYS);
        category.setDescription("Toys that defy gravity");

        when(categoryRepository.findByType(CategoryType.ANTI_GRAVITY_TOYS)).thenReturn(Optional.empty());

        CategoryEntity savedCategory = new CategoryEntity();
        savedCategory.setId(1L);
        savedCategory.setType(CategoryType.ANTI_GRAVITY_TOYS);
        savedCategory.setDescription("Toys that defy gravity");
        when(categoryRepository.save(category)).thenReturn(savedCategory);


        CategoryEntity result = categoryService.createCategory(category);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(categoryRepository).findByType(CategoryType.ANTI_GRAVITY_TOYS);
        verify(categoryRepository).save(category);
    }

    @Test
    void createCategory_WithDuplicateType_ThrowsException() {

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setType(CategoryType.COSMIC_FOOD);

        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setType(CategoryType.COSMIC_FOOD);
        newCategory.setDescription("Another cosmic food category");

        when(categoryRepository.findByType(CategoryType.COSMIC_FOOD)).thenReturn(Optional.of(existingCategory));


        assertThatThrownBy(() -> categoryService.createCategory(newCategory))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category with type COSMIC_FOOD already exists");

        verify(categoryRepository).findByType(CategoryType.COSMIC_FOOD);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getAllCategories_ReturnsAllCategories() {

        CategoryEntity category1 = new CategoryEntity();
        category1.setId(1L);
        category1.setType(CategoryType.ANTI_GRAVITY_TOYS);

        CategoryEntity category2 = new CategoryEntity();
        category2.setId(2L);
        category2.setType(CategoryType.COSMIC_FOOD);

        CategoryEntity category3 = new CategoryEntity();
        category3.setId(3L);
        category3.setType(CategoryType.SPACE_THINGIES);

        List<CategoryEntity> categories = Arrays.asList(category1, category2, category3);
        when(categoryRepository.findAll()).thenReturn(categories);


        List<CategoryEntity> result = categoryService.getAllCategories();


        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(category1, category2, category3);
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_WhenExists_ReturnsCategory() {

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setType(CategoryType.ANTI_GRAVITY_TOYS);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));


        Optional<CategoryEntity> result = categoryService.getCategoryById(1L);


        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getType()).isEqualTo(CategoryType.ANTI_GRAVITY_TOYS);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_WhenNotExists_ReturnsEmpty() {

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());


        Optional<CategoryEntity> result = categoryService.getCategoryById(999L);


        assertThat(result).isEmpty();
        verify(categoryRepository).findById(999L);
    }

    @Test
    void getCategoryByType_WhenExists_ReturnsCategory() {

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setType(CategoryType.COSMIC_FOOD);

        when(categoryRepository.findByType(CategoryType.COSMIC_FOOD)).thenReturn(Optional.of(category));


        Optional<CategoryEntity> result = categoryService.getCategoryByType(CategoryType.COSMIC_FOOD);


        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(CategoryType.COSMIC_FOOD);
        verify(categoryRepository).findByType(CategoryType.COSMIC_FOOD);
    }

    @Test
    void getCategoryByType_WhenNotExists_ReturnsEmpty() {

        when(categoryRepository.findByType(CategoryType.SPACE_THINGIES)).thenReturn(Optional.empty());


        Optional<CategoryEntity> result = categoryService.getCategoryByType(CategoryType.SPACE_THINGIES);


        assertThat(result).isEmpty();
        verify(categoryRepository).findByType(CategoryType.SPACE_THINGIES);
    }

    @Test
    void searchCategoriesByDescription_ReturnsMatchingCategories() {

        CategoryEntity category1 = new CategoryEntity();
        category1.setId(1L);
        category1.setDescription("Space toys for cats");

        CategoryEntity category2 = new CategoryEntity();
        category2.setId(2L);
        category2.setDescription("Space food items");

        List<CategoryEntity> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findByDescriptionContaining("Space")).thenReturn(categories);


        List<CategoryEntity> result = categoryService.searchCategoriesByDescription("Space");


        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(category1, category2);
        verify(categoryRepository).findByDescriptionContaining("Space");
    }

    @Test
    void searchCategoriesByDescription_NoMatches_ReturnsEmptyList() {

        when(categoryRepository.findByDescriptionContaining("NonExistent")).thenReturn(Arrays.asList());


        List<CategoryEntity> result = categoryService.searchCategoriesByDescription("NonExistent");


        assertThat(result).isEmpty();
        verify(categoryRepository).findByDescriptionContaining("NonExistent");
    }

    @Test
    void updateCategory_WhenExists_UpdatesSuccessfully() {

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setType(CategoryType.ANTI_GRAVITY_TOYS);
        existingCategory.setDescription("Old description");

        CategoryEntity updatedData = new CategoryEntity();
        updatedData.setType(CategoryType.COSMIC_FOOD);
        updatedData.setDescription("New description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByType(CategoryType.COSMIC_FOOD)).thenReturn(Optional.empty());

        CategoryEntity savedCategory = new CategoryEntity();
        savedCategory.setId(1L);
        savedCategory.setType(CategoryType.COSMIC_FOOD);
        savedCategory.setDescription("New description");
        when(categoryRepository.save(existingCategory)).thenReturn(savedCategory);


        CategoryEntity result = categoryService.updateCategory(1L, updatedData);


        assertThat(existingCategory.getType()).isEqualTo(CategoryType.COSMIC_FOOD);
        assertThat(existingCategory.getDescription()).isEqualTo("New description");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findByType(CategoryType.COSMIC_FOOD);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateCategory_WhenNotExists_ThrowsException() {

        CategoryEntity updatedData = new CategoryEntity();
        updatedData.setType(CategoryType.COSMIC_FOOD);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> categoryService.updateCategory(999L, updatedData))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");

        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WithDuplicateType_ThrowsException() {
        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setType(CategoryType.ANTI_GRAVITY_TOYS);

        CategoryEntity anotherCategory = new CategoryEntity();
        anotherCategory.setId(2L);
        anotherCategory.setType(CategoryType.COSMIC_FOOD);

        CategoryEntity updatedData = new CategoryEntity();
        updatedData.setType(CategoryType.COSMIC_FOOD);
        updatedData.setDescription("Updated description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByType(CategoryType.COSMIC_FOOD)).thenReturn(Optional.of(anotherCategory));


        assertThatThrownBy(() -> categoryService.updateCategory(1L, updatedData))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Error updating category with id: 1")
                .hasCauseInstanceOf(RuntimeException.class)
                .cause()
                .hasMessageContaining("Another category with type COSMIC_FOOD already exists");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findByType(CategoryType.COSMIC_FOOD);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WithSameTypeAsCurrent_UpdatesSuccessfully() {

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setType(CategoryType.ANTI_GRAVITY_TOYS);
        existingCategory.setDescription("Old description");

        CategoryEntity updatedData = new CategoryEntity();
        updatedData.setType(CategoryType.ANTI_GRAVITY_TOYS); // Same type
        updatedData.setDescription("New description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByType(CategoryType.ANTI_GRAVITY_TOYS)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);


        CategoryEntity result = categoryService.updateCategory(1L, updatedData);


        assertThat(existingCategory.getDescription()).isEqualTo("New description");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findByType(CategoryType.ANTI_GRAVITY_TOYS);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void deleteCategory_WhenExists_DeletesSuccessfully() {

        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);


        assertThatCode(() -> categoryService.deleteCategory(1L))
                .doesNotThrowAnyException();

        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_WhenNotExists_ThrowsException() {

        when(categoryRepository.existsById(999L)).thenReturn(false);


        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");

        verify(categoryRepository).existsById(999L);
        verify(categoryRepository, never()).deleteById(any());
    }
}