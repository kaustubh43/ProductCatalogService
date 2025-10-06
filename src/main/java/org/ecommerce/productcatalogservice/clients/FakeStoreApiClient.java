package org.ecommerce.productcatalogservice.clients;

import io.micrometer.common.lang.Nullable;
import org.ecommerce.productcatalogservice.dtos.FakeStoreProductDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FakeStoreApiClient {

    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public FakeStoreApiClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public FakeStoreProductDto getProductById(Long id) {
        ResponseEntity<FakeStoreProductDto> response =
                requestForEntity(HttpMethod.GET,
                        "http://fakestoreapi.com/products/{id}",
                        null,
                        FakeStoreProductDto.class,
                        id);
        if(validateResponse(response)) {
            return response.getBody();
        }
        return null;
    }

    public List<Product> getAllProducts(){
        ResponseEntity<FakeStoreProductDto[]> response =
                requestForEntity(
                        HttpMethod.GET,
                        "http://fakestoreapi.com/products/",
                        null,
                        FakeStoreProductDto[].class
                );
        if(validateResponse(response)) {
            return Arrays.stream(response.getBody())
                    .map(this::from)
                    .toList();
        }
        return null;
    }

    public Product createProduct(ProductDto productDto) {
        FakeStoreProductDto fakeStoreProductDto = from(productDto);
        ResponseEntity<FakeStoreProductDto> responseEntity = requestForEntity(
                HttpMethod.POST,
                "https://fakestoreapi.com/products",
                fakeStoreProductDto,
                FakeStoreProductDto.class
        );
        if(validateResponse(responseEntity)) {
            return from(responseEntity.getBody());
        }
        return null;
    }


    public Product replaceProduct(Product product, Long id){
        FakeStoreProductDto requestDto = FakeStoreProductDto.builder()
                .id(id)
                .title(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .category(product.getCategory().getName())
                .image(product.getImageUrl())
                .build();
        ResponseEntity<FakeStoreProductDto> response = requestForEntity(
                HttpMethod.PUT,
                "http://fakestoreapi.com/products/{id}",
                requestDto,
                FakeStoreProductDto.class,
                id
        );
        if(validateResponse(response)) {
            return from(response.getBody());
        }
        return null;
    }

    public Product deleteProductById(Long id) {
        ResponseEntity<FakeStoreProductDto> response = requestForEntity(
                HttpMethod.DELETE,
                "http://fakestoreapi.com/products/{id}",
                null,
                FakeStoreProductDto.class,
                id
        );
        return from(response.getBody());
    }

    private static <T> boolean validateResponse(ResponseEntity<T> response) {
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
    }

    private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request,
                                                   Class<T> responseType,
                                                   Object... uriVariables)
            throws RestClientException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }

    private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request,
                                                   Class<T> responseType)
            throws RestClientException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor);
    }

    private Product from(FakeStoreProductDto dto){
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

    private FakeStoreProductDto from(ProductDto productDto) {
        FakeStoreProductDto dto = FakeStoreProductDto.builder()
                .id(productDto.getId())
                .title(productDto.getName())
                .description(productDto.getDescription())
                .category(productDto.getCategoryDto().getName())
                .image(productDto.getImageUrl())
                .price(productDto.getPrice())
                .build();
        return dto;
    }
}
