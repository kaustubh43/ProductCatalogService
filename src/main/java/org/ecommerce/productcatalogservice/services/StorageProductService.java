package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.ecommerce.productcatalogservice.repositories.CategoryRepository;
import org.ecommerce.productcatalogservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@Primary
public class StorageProductService implements IProductService {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;

    @Autowired
    public StorageProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Gets a single product by the id else returns null.
     * @param id id of the product.
     * @return Product bearing id
     */
    @Override
    public Product getProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.orElse(null);
    }

    /**
     * Gets all the products
     * @return List of Products
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Creates a new Product
     * @param productDto Input to create the Product
     * @return returns the newly created and persisted Product
     */
    @Override
    public Product createProduct(ProductDto productDto) {
        Optional<Product>  productOptional = productRepository.findByName(productDto.getName());
        if(productOptional.isPresent()){
            return null;
        }
        Optional<Category> category = categoryRepository.findByName(productDto.getCategoryDto().getName());
        if(category.isEmpty()){
            return null; // Todo: refactor to create a new category if not found
        }
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .imageUrl(productDto.getImageUrl())
                .state(State.ACTIVE)
                .category(category.orElse(null))
                .build();
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    /**
     * Replaces a product entirely (POST)
     * @param product Product to be replaced
     * @param id id of the Product to be replaced
     * @return Replaced Product
     */
    @Override
    public Product replaceProduct(Product product, Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return null;
        }
        return productRepository.save(product);
    }

    /**
     * Deletes a product, sets state as DELETED
     * @param id id of the Product to be deleted
     * @return Deleted Product
     */
    @Override
    public Product deleteProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return null;
        }
        Product product = productOptional.get();
        product.setState(State.DELETED);
        return productRepository.save(product);
    }
}
