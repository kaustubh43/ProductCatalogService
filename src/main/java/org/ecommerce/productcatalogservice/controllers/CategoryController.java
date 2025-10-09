package org.ecommerce.productcatalogservice.controllers;


import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling category-related endpoints.
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private ICategoryService categoryService;

    @Autowired
    public void setCategoryService(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get Category by their ID passed as a path variable.
     * @return Category object
     */
    @GetMapping("/{id}")
    ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if(category == null) {
            throw new  RuntimeException("Something went wrong on our side, Category could not be found");
        }
        return new ResponseEntity<>(from(category), HttpStatus.OK);
    }

    /**
     * Post a new category
     * @return CategoryDto
     */
    @PostMapping
    ResponseEntity<CategoryDto> createCategory(@RequestBody Category category){
        Category created = categoryService.createCategory(category.getName(), category.getDescription());
        if(created == null) {
            throw new RuntimeException("Something went wrong on our side, Category could not be created");
        }
        return new ResponseEntity<>(from(created), HttpStatus.CREATED);
    }

    /**
     * Update an existing category
     * @return CategoryDto
     */
    @PutMapping("/update")
    ResponseEntity<CategoryDto> updateCategory(@RequestBody Category category){
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Patch an existing category
     * @return CategoryDto
     */
    @PatchMapping("/patch")
    ResponseEntity<CategoryDto> patchCategory(@RequestBody Category category){
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Delete an existing category
     * @return  CategoryDto
     */
    @DeleteMapping("/delete")
    ResponseEntity<CategoryDto> deleteCategory(@RequestBody Category category){
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * @return All categories
     */
    @GetMapping
    ResponseEntity<List<CategoryDto>> getAllCategories(){
        List<Category> categories = categoryService.getCategories();
        if(categories == null || categories.isEmpty()) {
            throw new RuntimeException("Something went wrong on our side, Categories could not be found");
        }
        return new ResponseEntity<>(categories.stream().map(this::from).collect(Collectors.toList()), HttpStatus.OK);
    }

    private CategoryDto from(Category category) {
        CategoryDto dto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
        return dto;
    }
}
