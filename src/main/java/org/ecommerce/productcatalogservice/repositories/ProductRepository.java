package org.ecommerce.productcatalogservice.repositories;

import org.ecommerce.productcatalogservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to provide CRUD operations and query methods for Product.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

}
