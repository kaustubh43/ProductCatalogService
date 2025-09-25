package org.ecommerce.productcatalogservice.controllers;


import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.State;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling category-related endpoints.
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    /**
     * Get Category by their ID passed as a path variable.
     * @return Category object
     */
    @GetMapping("/{id}")
    CategoryDto getCategoryById(@PathVariable Long id) {
        Category category = new Category();
        category.setId(id);
        return null;
    }

    /**
     * Post a new category
     * @return CategoryDto
     */
    @PostMapping("/")
    CategoryDto createCategory(@RequestBody Category category){
        return null;
    }

    /**
     * Update an existing category
     * @return CategoryDto
     */
    @PutMapping("/update")
    CategoryDto updateCategory(@RequestBody Category category){
        return null;
    }

    /**
     * Patch an existing category
     * @return CategoryDto
     */
    @PatchMapping("/patch")
    CategoryDto patchCategory(@RequestBody Category category){
        return null;
    }

    /**
     * Delete an existing category
     * @return  CategoryDto
     */
    @DeleteMapping("/delete")
    CategoryDto deleteCategory(@RequestBody Category category){
        category.setState(State.DELETED);
        return null;
    }
}
