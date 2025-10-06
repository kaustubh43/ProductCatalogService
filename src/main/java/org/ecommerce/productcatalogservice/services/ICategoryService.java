package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.Category;

import javax.management.Descriptor;
import java.util.List;

public interface ICategoryService {
    public Category getCategoryById(Long id);

    public Category getCategoryByName(String name);

    public Category createCategory(Long id, String name, String description);

    public List<Category> getCategories();

}
