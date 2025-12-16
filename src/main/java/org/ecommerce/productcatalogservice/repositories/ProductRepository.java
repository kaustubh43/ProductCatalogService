package org.ecommerce.productcatalogservice.repositories;

import org.ecommerce.productcatalogservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to provide CRUD operations and query methods for Product.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByName(String name);

    Page<Product> findAllByName(String name, Pageable pageable);
}
