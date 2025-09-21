package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.dtos.FakeStoreProductDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class FakeStoreProductService implements IProductService {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Override
    public Product getProductById(Long id) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDto> response =
                restTemplate.getForEntity("http://fakestoreapi.com/products/{id}",
                        FakeStoreProductDto.class,
                        id);
        if(response.getStatusCode().is2xxSuccessful() &&  response.getBody() != null) {
            return productFromFakeStoreProductDto(response.getBody());
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDto[]> response =
                restTemplate.getForEntity("http://fakestoreapi.com/products/",
                FakeStoreProductDto[].class);
        if(response.getStatusCode().is2xxSuccessful() &&  response.getBody() != null) {
            return Arrays.stream(response.getBody())
                    .map(this::productFromFakeStoreProductDto)
                    .toList();
        }
        return List.of();
    }

    @Override
    public Product createProduct(ProductDto productDto) {
        return null;
    }

    private Product productFromFakeStoreProductDto(FakeStoreProductDto dto){
        if (dto == null) return null;
        return Product.builder()
                .id(dto.getId())
                .createdAt(new Date())
                .lastUpdatedAt(new Date())
                .state(State.ACTIVE)
                .name(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImage())
                .price(dto.getPrice())
                .category(Category.builder().name(dto.getCategory()).build())
                .build();
    }
}
