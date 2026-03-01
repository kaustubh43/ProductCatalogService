package org.ecommerce.productcatalogservice.services;

import org.apache.commons.lang.NotImplementedException;
import org.ecommerce.productcatalogservice.clients.FakeStoreApiClient;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakeStoreProductServiceTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private FakeStoreApiClient fakeStoreApiClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private FakeStoreProductService fakeStoreProductService;

    private FakeStoreProductDto buildFakeDto(Long id, String title, double price, String category) {
        return FakeStoreProductDto.builder()
                .id(id).title(title).price(price)
                .description(title + " desc").category(category)
                .image("http://img/" + id)
                .build();
    }

    private Product buildProduct(Long id, String name, double price) {
        return Product.builder()
                .id(id).name(name).price(price)
                .category(Category.builder().name("Test").build())
                .state(State.ACTIVE)
                .build();
    }

    private ProductDto buildProductDto() {
        return ProductDto.builder()
                .name("Phone").description("desc").price(599.0)
                .imageUrl("http://img/1")
                .categoryDto(CategoryDto.builder().id(1L).name("Electronics").description("desc").build())
                .build();
    }

    // ──── getProductById ────

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @BeforeEach
        void setUp() {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        }

        @Test
        @DisplayName("returns product from cache when cached")
        void whenCacheHit_returnsFromCache() {
            FakeStoreProductDto cached = buildFakeDto(1L, "Phone", 599, "Electronics");
            when(hashOperations.get("PRODUCTS", 1L)).thenReturn(cached);

            Product result = fakeStoreProductService.getProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Phone");
            verify(fakeStoreApiClient, never()).getProductById(anyLong());
        }

        @Test
        @DisplayName("fetches from API and caches when not in cache")
        void whenCacheMiss_fetchesFromApiAndCaches() {
            FakeStoreProductDto dto = buildFakeDto(2L, "Laptop", 999, "Electronics");
            when(hashOperations.get("PRODUCTS", 2L)).thenReturn(null);
            when(fakeStoreApiClient.getProductById(2L)).thenReturn(dto);

            Product result = fakeStoreProductService.getProductById(2L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Laptop");
            verify(hashOperations).put("PRODUCTS", 2L, dto);
        }

        @Test
        @DisplayName("returns null when not in cache and API returns null")
        void whenCacheMissAndApiReturnsNull_returnsNull() {
            when(hashOperations.get("PRODUCTS", 3L)).thenReturn(null);
            when(fakeStoreApiClient.getProductById(3L)).thenReturn(null);

            Product result = fakeStoreProductService.getProductById(3L);

            assertThat(result).isNull();
            verify(hashOperations, never()).put(anyString(), any(), any());
        }
    }

    // ──── getAllProducts ────

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("returns products from API client")
        void returnsProducts() {
            List<Product> products = List.of(buildProduct(1L, "A", 10), buildProduct(2L, "B", 20));
            when(fakeStoreApiClient.getAllProducts()).thenReturn(products);

            List<Product> result = fakeStoreProductService.getAllProducts();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("returns empty list when API returns null")
        void returnsEmptyListWhenNull() {
            when(fakeStoreApiClient.getAllProducts()).thenReturn(null);

            List<Product> result = fakeStoreProductService.getAllProducts();

            assertThat(result).isEmpty();
        }
    }

    // ──── replaceProduct ────

    @Nested
    @DisplayName("replaceProduct")
    class ReplaceProduct {

        @Test
        @DisplayName("returns replaced product from API client")
        void replacesProduct() {
            Product product = buildProduct(1L, "Phone", 500);
            when(fakeStoreApiClient.replaceProduct(product, 1L)).thenReturn(product);

            Product result = fakeStoreProductService.replaceProduct(product, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Phone");
        }

        @Test
        @DisplayName("returns null when API returns null")
        void returnsNullWhenApiFails() {
            Product product = buildProduct(1L, "Phone", 500);
            when(fakeStoreApiClient.replaceProduct(product, 1L)).thenReturn(null);

            Product result = fakeStoreProductService.replaceProduct(product, 1L);

            assertThat(result).isNull();
        }
    }

    // ──── deleteProductById ────

    @Nested
    @DisplayName("deleteProductById")
    class DeleteProductById {

        @Test
        @DisplayName("returns deleted product")
        void deletesProduct() {
            Product product = buildProduct(1L, "Phone", 500);
            when(fakeStoreApiClient.deleteProductById(1L)).thenReturn(product);

            Product result = fakeStoreProductService.deleteProductById(1L);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("returns null when API returns null")
        void returnsNullWhenApiFails() {
            when(fakeStoreApiClient.deleteProductById(99L)).thenReturn(null);

            Product result = fakeStoreProductService.deleteProductById(99L);

            assertThat(result).isNull();
        }
    }

    // ──── createProduct ────

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("creates product via API client")
        void createsProduct() {
            ProductDto dto = buildProductDto();
            Product created = buildProduct(1L, "Phone", 599);
            when(fakeStoreApiClient.createProduct(dto)).thenReturn(created);

            Product result = fakeStoreProductService.createProduct(dto);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Phone");
        }

        @Test
        @DisplayName("returns null when API returns null")
        void returnsNullOnFailure() {
            ProductDto dto = buildProductDto();
            when(fakeStoreApiClient.createProduct(dto)).thenReturn(null);

            Product result = fakeStoreProductService.createProduct(dto);

            assertThat(result).isNull();
        }
    }

    // ──── getDetailsBasedOnUserRole ────

    @Test
    @DisplayName("getDetailsBasedOnUserRole throws NotImplementedException")
    void getDetailsBasedOnUserRole_throwsNotImplementedException() {
        assertThatThrownBy(() -> fakeStoreProductService.getDetailsBasedOnUserRole(1L, 1L))
                .isInstanceOf(NotImplementedException.class);
    }
}

