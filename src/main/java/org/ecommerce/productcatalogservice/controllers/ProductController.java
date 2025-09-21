package org.ecommerce.productcatalogservice.controllers;

import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling product-related endpoints.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * Get Product by their ID passed as a path variable.
     * @return ProductDto
     */
    @GetMapping("/{id}")
    ProductDto getProductById(@PathVariable("id") Long productId){
        Product product = new Product();
        product.setId(productId);
        return null;
    }

    /**
     * Add a product.
     * @return ProductDto
     */
    @PostMapping("/")
    ProductDto addProduct(@RequestBody Product product){
        return null; // Todo: Create a product and all other functions
    }

    /**
     * Put a product
     * @return ProductDto
     */
    @PutMapping("/update")
    ProductDto updateProduct(@RequestBody Product product){
        return null;
    }

    /**
     * Patch a product
     * @return ProductDto
     */
    @PatchMapping("/patch")
    public ProductDto patchProduct(@RequestBody Product product){
        return null;
    }

    /**
     * Delete a product
     * @return ProductDto
     */
    @DeleteMapping("/delete")
    public ProductDto deleteProduct(@RequestBody Product product){
        product.setState(State.DELETED);
        return null;
    }
}
