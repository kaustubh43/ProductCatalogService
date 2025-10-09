package org.ecommerce.productcatalogservice.repositories;

import org.ecommerce.productcatalogservice.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Override
    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);

}
