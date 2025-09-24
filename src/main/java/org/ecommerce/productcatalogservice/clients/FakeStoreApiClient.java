package org.ecommerce.productcatalogservice.clients;

import io.micrometer.common.lang.Nullable;
import org.ecommerce.productcatalogservice.dtos.FakeStoreProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    private static boolean validateResponse(ResponseEntity<FakeStoreProductDto> response) {
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
}
