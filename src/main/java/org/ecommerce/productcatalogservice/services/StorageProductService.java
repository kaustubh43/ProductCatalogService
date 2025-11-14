package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.dtos.UserDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.ecommerce.productcatalogservice.repositories.CategoryRepository;
import org.ecommerce.productcatalogservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class StorageProductService implements IProductService {

    private final RestTemplate restTemplate;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public StorageProductService(CategoryRepository categoryRepository, ProductRepository productRepository, RestTemplate restTemplate) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
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
        return productRepository.save(product);
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

    @Override
    public Product getDetailsBasedOnUserRole(Long productId, Long userId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            return null;
        }
        // Todo: Add check for product is listed or not, add a field in product.
        UserDto userDto = restTemplate.getForEntity(
                "http://UserAuthenticationService/users/{userId}",
                UserDto.class,
                userId).getBody();

        if(userDto != null){
            System.out.println("User Email: " + userDto.getEmail());

            // Check user authorities/roles and then return the product.
            if(userDto.getRoles() != null) {
                boolean isAdmin = userDto.getRoles().stream()
                        .anyMatch(role -> role.equalsIgnoreCase("ADMIN"));
                if (isAdmin) {
                    return productOptional.get();
                } else {
                    // For non-admin users, check if the product is ACTIVE
                    if (productOptional.get().getState() == State.ACTIVE) {
                        return productOptional.get(); // Listed Item
                    } else {
                        return null; // Product is not accessible to non-admin users, Unlisted Item
                    }
                }
            }
        }
        return null;
    }
}
