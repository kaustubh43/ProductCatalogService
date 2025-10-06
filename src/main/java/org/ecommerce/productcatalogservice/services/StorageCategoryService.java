package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing categories using in-memory storage.
 * This class provides stub implementations for category-related operations.
 * All methods currently return default values and should be implemented as needed.
 */
@Service
@Primary
public class StorageCategoryService implements ICategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id the unique identifier of the category
     * @return the Category object if found, otherwise null
     */
    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.orElse(null);
    }

    /**
     * Retrieves a category by its name.
     *
     * @param name the name of the category
     * @return the Category object if found, otherwise null
     */
    @Override
    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        return category.orElse(null);
    }

    /** Creates a category
     * @param name
     * @return
     */
    @Override
    public Category createCategory(Long id, String name, String description) {
        Category category = Category.builder()
                .id(id)
                .name(name)
                .description(description).build();
        Category saved = categoryRepository.save(category);
        return saved;
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of all Category objects, or an empty list if none exist
     */
    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
}
