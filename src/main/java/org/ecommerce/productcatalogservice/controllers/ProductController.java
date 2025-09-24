package org.ecommerce.productcatalogservice.controllers;

import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

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
     * @return ResponseEntity<ProductDto>
     */
    @GetMapping("/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId){

        if (productId <= 0) {
            throw new IllegalArgumentException("Product Id must be greater than 0");
        } else if (productId > 20){
            throw new RuntimeException("Something went wrong on our side");
        }
        Product product = productService.getProductById(productId);
        ProductDto productDto = getProductDtoFromProduct(product);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//            headers.add("someString1", "someString2");
        return new ResponseEntity<>(productDto, headers, HttpStatus.OK);
    }

    /**
     * Get all Products
     * @return ResponseEntity<List<ProductDto>>
     */
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
    @PostMapping("/add")
    ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED); // Todo: Create a product and all other functions
    }

    /**
     * Put a product
     * @return ResponseEntity<ProductDto>
     */
    @PutMapping("/update/{id}")
    ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto,@PathVariable("id") Long productId){
        try{
            Product product = getProductFromProductDto(productDto, productId);
            Product replacedProduct = productService.replaceProduct(product, productId);
            ProductDto response = getProductDtoFromProduct(replacedProduct);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Patch a product
     * @return ResponseEntity<ProductDto>
     */
    @PatchMapping("/patch/{id}")
    public ResponseEntity<ProductDto> patchProduct(@RequestBody ProductDto productDto,@PathVariable("id") Long productId){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Delete a product
     * @return ProductDto
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@RequestBody ProductDto productDto,@PathVariable("id") Long productId){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Helper method to convert Product -> ProductDto
     * @param product Product Domain Object
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

    /**
     * Helper method to convert ProductDto -> Product
     * @param productDto ProductDto Object
     * @param productId Long id of the product
     * @return Product
     */
    private Product getProductFromProductDto(ProductDto productDto, Long productId) {
        return Product.builder()
                .id(productId)
                .name(productDto.getName())
                .imageUrl(productDto.getImageUrl())
                .price(productDto.getPrice())
                .category(Category.builder().name(productDto.getCategoryDto().getName()).build())
                .build();
    }
}
