package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.dtos.SortOrder;
import org.ecommerce.productcatalogservice.dtos.SortParameters;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageSearchService implements ISearchService {

    private ProductRepository productRepository;

    @Autowired
    public StorageSearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> searchProducts(String query, Integer pageSize, Integer pageNumber, List<SortParameters> sortParameters) {
        Sort sort = null;
        if(!sortParameters.isEmpty()) {
            if(sortParameters.get(0).getSortOrder().equals(SortOrder.ASCENDING)) {
                sort = Sort.by(sortParameters.get(0).getSortCriteria());
            } else{
                sort = Sort.by(sortParameters.get(0).getSortCriteria()).descending();
            }
        }
        for(int i = 1; i < sortParameters.size(); i++) {
            if(sortParameters.get(i).getSortOrder().equals(SortOrder.ASCENDING)) {
                sort = sort.and(Sort.by(sortParameters.get(i).getSortCriteria()));
            } else{
                sort = sort.and(Sort.by(sortParameters.get(i).getSortCriteria()).descending());
            }
        }
        return productRepository.findAllByName(query, PageRequest.of(pageNumber, pageSize, sort));
    }
}
