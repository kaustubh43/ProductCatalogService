package org.ecommerce.productcatalogservice.clients;

import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.dtos.FakeStoreProductDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakeStoreApiClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FakeStoreApiClient fakeStoreApiClient;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
    }

    // ──── Helpers ────

    private FakeStoreProductDto buildFakeDto(Long id, String title, double price, String category) {
        return FakeStoreProductDto.builder()
                .id(id).title(title).price(price)
                .description(title + " desc").category(category)
                .image("http://img/" + id)
                .build();
    }


    // ──── getProductById ────

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("returns DTO when API responds 200 with body")
        @SuppressWarnings("unchecked")
        void whenApiReturns200_returnsDto() {
            FakeStoreProductDto dto = buildFakeDto(1L, "Phone", 599, "Electronics");
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(dto, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class),
                    any(Object[].class)))
                    .thenReturn(response);

            FakeStoreProductDto result = fakeStoreApiClient.getProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Phone");
        }

        @Test
        @DisplayName("returns null when API responds with null body")
        @SuppressWarnings("unchecked")
        void whenApiReturnsNullBody_returnsNull() {
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class),
                    any(Object[].class)))
                    .thenReturn(response);

            FakeStoreProductDto result = fakeStoreApiClient.getProductById(1L);

            assertThat(result).isNull();
        }
    }

    // ──── getAllProducts ────

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("returns list of products when API responds 200")
        @SuppressWarnings("unchecked")
        void whenApiReturns200_returnsProducts() {
            FakeStoreProductDto[] dtos = {
                    buildFakeDto(1L, "Phone", 599, "Electronics"),
                    buildFakeDto(2L, "Book", 15, "Books")
            };
            ResponseEntity<FakeStoreProductDto[]> response = new ResponseEntity<>(dtos, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class)))
                    .thenReturn(response);

            List<Product> result = fakeStoreApiClient.getAllProducts();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Phone");
            assertThat(result.get(0).getCategory().getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("returns null when API responds with null body")
        @SuppressWarnings("unchecked")
        void whenApiReturnsNullBody_returnsNull() {
            ResponseEntity<FakeStoreProductDto[]> response = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class)))
                    .thenReturn(response);

            List<Product> result = fakeStoreApiClient.getAllProducts();

            assertThat(result).isNull();
        }
    }

    // ──── createProduct ────

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("returns product when API responds 200")
        @SuppressWarnings("unchecked")
        void whenApiReturns200_returnsProduct() {
            FakeStoreProductDto responseDto = buildFakeDto(1L, "Phone", 599, "Electronics");
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(responseDto, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class)))
                    .thenReturn(response);

            ProductDto dto = ProductDto.builder()
                    .id(1L).name("Phone").price(599).description("desc")
                    .imageUrl("http://img/1")
                    .categoryDto(CategoryDto.builder().id(1L).name("Electronics").description("desc").build())
                    .build();

            Product result = fakeStoreApiClient.createProduct(dto);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Phone");
        }

        @Test
        @DisplayName("returns null when API responds with null body")
        @SuppressWarnings("unchecked")
        void whenApiReturnsNullBody_returnsNull() {
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class)))
                    .thenReturn(response);

            ProductDto dto = ProductDto.builder()
                    .id(1L).name("Phone").price(599).description("desc")
                    .imageUrl("http://img/1")
                    .categoryDto(CategoryDto.builder().id(1L).name("Electronics").description("desc").build())
                    .build();

            Product result = fakeStoreApiClient.createProduct(dto);

            assertThat(result).isNull();
        }
    }

    // ──── replaceProduct ────

    @Nested
    @DisplayName("replaceProduct")
    class ReplaceProduct {

        @Test
        @DisplayName("returns replaced product when API responds 200")
        @SuppressWarnings("unchecked")
        void whenApiReturns200_returnsProduct() {
            FakeStoreProductDto responseDto = buildFakeDto(1L, "New Phone", 699, "Electronics");
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(responseDto, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class),
                    any(Object[].class)))
                    .thenReturn(response);

            Product product = Product.builder()
                    .id(1L).name("New Phone").price(699)
                    .category(Category.builder().name("Electronics").build())
                    .imageUrl("http://img/1")
                    .build();

            Product result = fakeStoreApiClient.replaceProduct(product, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("New Phone");
        }

        @Test
        @DisplayName("returns null when API responds with null body")
        @SuppressWarnings("unchecked")
        void whenApiReturnsNullBody_returnsNull() {
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class),
                    any(Object[].class)))
                    .thenReturn(response);

            Product product = Product.builder()
                    .id(1L).name("Phone").price(500)
                    .category(Category.builder().name("Electronics").build())
                    .imageUrl("http://img/1")
                    .build();

            Product result = fakeStoreApiClient.replaceProduct(product, 1L);

            assertThat(result).isNull();
        }
    }

    // ──── deleteProductById ────

    @Nested
    @DisplayName("deleteProductById")
    class DeleteProductById {

        @Test
        @DisplayName("returns deleted product when API responds with body")
        @SuppressWarnings("unchecked")
        void whenApiReturnsBody_returnsProduct() {
            FakeStoreProductDto responseDto = buildFakeDto(1L, "Phone", 599, "Electronics");
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(responseDto, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class),
                    any(Object[].class)))
                    .thenReturn(response);

            Product result = fakeStoreApiClient.deleteProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Phone");
            assertThat(result.getState()).isEqualTo(State.ACTIVE);
        }

        @Test
        @DisplayName("returns null when API responds with null body")
        @SuppressWarnings("unchecked")
        void whenApiReturnsNullBody_returnsNull() {
            ResponseEntity<FakeStoreProductDto> response = new ResponseEntity<>(null, HttpStatus.OK);

            when(restTemplate.httpEntityCallback(any(), any(Class.class))).thenReturn(mock(RequestCallback.class));
            when(restTemplate.responseEntityExtractor(any(Class.class))).thenReturn(mock(ResponseExtractor.class));
            when(restTemplate.execute(
                    anyString(), any(HttpMethod.class),
                    nullable(RequestCallback.class), nullable(ResponseExtractor.class),
                    any(Object[].class)))
                    .thenReturn(response);

            Product result = fakeStoreApiClient.deleteProductById(1L);

            assertThat(result).isNull();
        }
    }
}

