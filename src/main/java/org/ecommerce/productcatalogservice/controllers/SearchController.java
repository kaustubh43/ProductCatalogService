package org.ecommerce.productcatalogservice.controllers;

import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.dtos.SearchRequestDto;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.services.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final ISearchService searchService;

    @Autowired
    public SearchController(ISearchService searchService) {
        this.searchService = searchService;
    }

    // Todo: Add filters for search.
    /**
     * Sort by multiple parameters.
     * Example payload
     * {
     *   "query": "Galaxy phone",
     *   "pageSize": 4,
     *   "pageNumber": 0,
     *   "sortParameters": [
     *     {
     *       "sortCriteria": "price",
     *       "sortOrder": "ASCENDING"
     *     },
     *     {
     *       "sortCriteria": "id",
     *       "sortOrder": "ASCENDING"
     *     }
     *   ]
     * }
     * @param searchRequestDto
     * @return
     */
    @PostMapping
    public Page<Product> searchProducts(@RequestBody SearchRequestDto searchRequestDto) {
        Page<Product> products = searchService.searchProducts(searchRequestDto.getQuery(),
                searchRequestDto.getPageSize(),
                searchRequestDto.getPageNumber(),
                searchRequestDto.getSortParameters());
        return products;
    }

    /**
     * Helper method to convert Product -> ProductDto
     * @param product Product Domain Object
     * @return ProductDto
     */
    private ProductDto from(Product product) {
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
