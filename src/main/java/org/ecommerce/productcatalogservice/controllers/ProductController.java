package org.ecommerce.productcatalogservice.controllers;

import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.ecommerce.productcatalogservice.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling product-related endpoints.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    /**
     * Get Product by their ID passed as a path variable.
     * @return ProductDto
     */
    @GetMapping("/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId){
        try {
            if (productId <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Product product = productService.getProductById(productId);
            ProductDto productDto = getProductDtoFromProduct(product);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("someString1", "someString2");
            return new ResponseEntity<>(productDto, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    ResponseEntity<List<ProductDto>> getAllProducts(){
        try{
            List<Product> products = productService.getAllProducts();

            List<ProductDto> productDtos = products.stream()
                    .map(this::getProductDtoFromProduct)
                    .toList();

            return new ResponseEntity<>(productDtos, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    /**
     * Helper method to convert Product -> ProductDto
     * @param product
     * @return ProductDto
     */
    private ProductDto getProductDtoFromProduct(Product product) {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(product.getCategory().getId())
                .name(product.getCategory().getName())
                .description(product.getCategory().getDescription())
                .build();

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .categoryDto(categoryDto)
                .build();
    }

}
