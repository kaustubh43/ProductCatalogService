package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class StorageSearchService implements ISearchService {

    private ProductRepository productRepository;

    @Autowired
    public StorageSearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> searchProducts(String query, Integer pageSize, Integer pageNumber) {
        Sort sort = Sort.by("price").and(Sort.by("id").descending());
        return productRepository.findAllByName(query, PageRequest.of(pageNumber, pageSize, sort));
    }
}
