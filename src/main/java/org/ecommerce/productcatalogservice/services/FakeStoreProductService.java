package org.ecommerce.productcatalogservice.services;

import io.micrometer.common.lang.Nullable;
import org.ecommerce.productcatalogservice.clients.FakeStoreApiClient;
import org.ecommerce.productcatalogservice.dtos.FakeStoreProductDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class FakeStoreProductService implements IProductService {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private FakeStoreApiClient fakeStoreApiClient;

    @Override
    public Product getProductById(Long id) {
        FakeStoreProductDto fakeStoreProductDto = fakeStoreApiClient.getProductById(id);
        if(fakeStoreProductDto != null) {
            return productFromFakeStoreProductDto(fakeStoreProductDto);
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = fakeStoreApiClient.getAllProducts();
        if(productList != null) {
            return productList;
        }
        return List.of();
    }

    @Override
    public Product replaceProduct(Product product, Long id) {
        Product replacedProduct = fakeStoreApiClient.replaceProduct(product, id);

        if(replacedProduct != null) {
            return replacedProduct;
        }
        return null;
    }

    @Override
    public Product deleteProductById(Long id) {
        Product deleted = fakeStoreApiClient.deleteProductById(id);
        if(deleted != null) {
            return deleted;
        }
        return null;
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

    private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }
}
