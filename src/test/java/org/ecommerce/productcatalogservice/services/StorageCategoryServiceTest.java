package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.repositories.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageCategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private StorageCategoryService storageCategoryService;

    private Category buildCategory(Long id, String name, String description) {
        Category cat = new Category();
        cat.setId(id);
        cat.setName(name);
        cat.setDescription(description);
        return cat;
    }

    // ──── getCategoryById ────

    @Nested
    @DisplayName("getCategoryById")
    class GetCategoryById {

        @Test
        @DisplayName("returns category when found")
        void whenCategoryExists_returnsCategory() {
            Category cat = buildCategory(1L, "Electronics", "Electronic items");
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

            Category result = storageCategoryService.getCategoryById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Electronics");
            verify(categoryRepository).findById(1L);
        }

        @Test
        @DisplayName("returns null when category not found")
        void whenCategoryDoesNotExist_returnsNull() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            Category result = storageCategoryService.getCategoryById(99L);

            assertThat(result).isNull();
        }
    }

    // ──── getCategoryByName ────

    @Nested
    @DisplayName("getCategoryByName")
    class GetCategoryByName {

        @Test
        @DisplayName("returns category when found by name")
        void whenNameExists_returnsCategory() {
            Category cat = buildCategory(1L, "Books", "All books");
            when(categoryRepository.findByName("Books")).thenReturn(Optional.of(cat));

            Category result = storageCategoryService.getCategoryByName("Books");

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Books");
        }

        @Test
        @DisplayName("returns null when name not found")
        void whenNameDoesNotExist_returnsNull() {
            when(categoryRepository.findByName("Ghost")).thenReturn(Optional.empty());

            Category result = storageCategoryService.getCategoryByName("Ghost");

            assertThat(result).isNull();
        }
    }

    // ──── createCategory ────

    @Nested
    @DisplayName("createCategory")
    class CreateCategory {

        @Test
        @DisplayName("creates and returns saved category")
        void createsCategory() {
            Category saved = buildCategory(1L, "Fashion", "Fashion items");
            when(categoryRepository.save(any(Category.class))).thenReturn(saved);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(saved));

            Category result = storageCategoryService.createCategory("Fashion", "Fashion items");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Fashion");
            verify(categoryRepository).save(any(Category.class));
        }
    }

    // ──── getCategories ────

    @Nested
    @DisplayName("getCategories")
    class GetCategories {

        @Test
        @DisplayName("returns list of categories")
        void returnsCategoriesList() {
            List<Category> cats = List.of(
                    buildCategory(1L, "Electronics", "desc1"),
                    buildCategory(2L, "Books", "desc2")
            );
            when(categoryRepository.findAll()).thenReturn(cats);

            List<Category> result = storageCategoryService.getCategories();

            assertThat(result).hasSize(2);
            verify(categoryRepository).findAll();
        }

        @Test
        @DisplayName("returns empty list when no categories")
        void returnsEmptyList() {
            when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

            List<Category> result = storageCategoryService.getCategories();

            assertThat(result).isEmpty();
        }
    }
}

