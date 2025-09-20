package org.ecommerce.productcatalogservice.controllers;


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
    Category getCategoryById(@PathVariable int id) {
        Category category = new Category();
        category.setId(id);
        return category;
    }

    /**
     * Post a new category
     * @return category
     */
    @PostMapping("/")
     Category createCategory(@RequestBody Category category){
        return category;
    }

    /**
     * Update an existing category
     */
    @PutMapping("/update")
    Category updateCategory(@RequestBody Category category){
        return category;
    }

    /**
     * Patch an existing category
     */
    @PatchMapping("/patch")
    Category patchCategory(@RequestBody Category category){
        return category;
    }

    /**
     * Delete an existing category
     */
    @DeleteMapping("/delete")
    Category deleteCategory(@RequestBody Category category){
        category.setState(State.DELETED);
        return category;
    }
}
