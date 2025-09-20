package org.ecommerce.productcatalogservice.controllers;

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
     * @return Product
     */
    @GetMapping("/{id}")
    Product getProductById(@PathVariable("id") Long productId){
        Product product = new Product();
        product.setId(productId);
        return product;
    }

    /**
     * Add a product.
     * @return product
     */
    @PostMapping("/")
    Product addProduct(@RequestBody Product product){
        return product;
    }

    /**
     * Put a product
     * @return product
     */
    @PutMapping("/update")
    Product updateProduct(@RequestBody Product product){
        return product;
    }

    /**
     * Patch a product
     * @return Product
     */
    @PatchMapping("/patch")
    Product patchProduct(@RequestBody Product product){
        return product;
    }

    /**
     * Delete a product
     * @return product
     */
    @DeleteMapping("/delete")
    Product deleteProduct(@RequestBody Product product){
        product.setState(State.DELETED);
        return product;
    }
}
