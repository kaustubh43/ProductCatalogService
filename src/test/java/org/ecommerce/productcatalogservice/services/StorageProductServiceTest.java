package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.dtos.UserDto;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.Role;
import org.ecommerce.productcatalogservice.models.State;
import org.ecommerce.productcatalogservice.repositories.CategoryRepository;
import org.ecommerce.productcatalogservice.repositories.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StorageProductService storageProductService;

    // ──── Helper builders ────

    private Category buildCategory(Long id, String name) {
        return Category.builder().id(id).name(name).description(name + " desc").build();
    }

    private Product buildProduct(Long id, String name, double price, Category category, State state) {
        return Product.builder()
                .id(id).name(name).description(name + " desc")
                .imageUrl("http://img/" + id).price(price)
                .category(category).state(state)
                .build();
    }

    private ProductDto buildProductDto(String name, double price, String categoryName) {
        return ProductDto.builder()
                .name(name).description(name + " desc")
                .imageUrl("http://img/" + name).price(price)
                .categoryDto(CategoryDto.builder().name(categoryName).build())
                .build();
    }

    private Role buildRole(String value) {
        Role role = new Role();
        role.setValue(value);
        return role;
    }

    // ──── getProductById ────

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("returns product when found")
        void whenProductExists_returnsProduct() {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599.99, cat, State.ACTIVE);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            Product result = storageProductService.getProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Phone");
            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("returns null when not found")
        void whenProductDoesNotExist_returnsNull() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            Product result = storageProductService.getProductById(99L);

            assertThat(result).isNull();
            verify(productRepository).findById(99L);
        }
    }

    // ──── getAllProducts ────

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("returns all products")
        void returnsAllProducts() {
            Category cat = buildCategory(1L, "Books");
            List<Product> products = List.of(
                    buildProduct(1L, "Book A", 10, cat, State.ACTIVE),
                    buildProduct(2L, "Book B", 15, cat, State.ACTIVE)
            );
            when(productRepository.findAll()).thenReturn(products);

            List<Product> result = storageProductService.getAllProducts();

            assertThat(result).hasSize(2);
            verify(productRepository).findAll();
        }

        @Test
        @DisplayName("returns empty list when no products exist")
        void returnsEmptyList() {
            when(productRepository.findAll()).thenReturn(Collections.emptyList());

            List<Product> result = storageProductService.getAllProducts();

            assertThat(result).isEmpty();
        }
    }

    // ──── createProduct ────

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("creates product successfully when product name is new and category exists")
        void whenNewProductAndCategoryExists_createsProduct() {
            ProductDto dto = buildProductDto("Laptop", 999.99, "Electronics");
            Category cat = buildCategory(1L, "Electronics");

            when(productRepository.findByName("Laptop")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(cat));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
                Product p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });

            Product result = storageProductService.createProduct(dto);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Laptop");
            assertThat(result.getState()).isEqualTo(State.ACTIVE);
            assertThat(result.getCategory()).isEqualTo(cat);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("returns null when product with same name already exists")
        void whenProductAlreadyExists_returnsNull() {
            ProductDto dto = buildProductDto("Phone", 599, "Electronics");
            Product existing = buildProduct(1L, "Phone", 599, buildCategory(1L, "Electronics"), State.ACTIVE);

            when(productRepository.findByName("Phone")).thenReturn(Optional.of(existing));

            Product result = storageProductService.createProduct(dto);

            assertThat(result).isNull();
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("returns null when category does not exist")
        void whenCategoryNotFound_returnsNull() {
            ProductDto dto = buildProductDto("Tablet", 399, "NonExistent");

            when(productRepository.findByName("Tablet")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("NonExistent")).thenReturn(Optional.empty());

            Product result = storageProductService.createProduct(dto);

            assertThat(result).isNull();
            verify(productRepository, never()).save(any());
        }
    }

    // ──── replaceProduct ────

    @Nested
    @DisplayName("replaceProduct")
    class ReplaceProduct {

        @Test
        @DisplayName("replaces product when it exists")
        void whenProductExists_replacesAndReturns() {
            Category cat = buildCategory(1L, "Electronics");
            Product existing = buildProduct(1L, "Old Phone", 400, cat, State.ACTIVE);
            Product replacement = buildProduct(1L, "New Phone", 500, cat, State.ACTIVE);

            when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(productRepository.save(replacement)).thenReturn(replacement);

            Product result = storageProductService.replaceProduct(replacement, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("New Phone");
            verify(productRepository).save(replacement);
        }

        @Test
        @DisplayName("returns null when product to replace does not exist")
        void whenProductDoesNotExist_returnsNull() {
            Product replacement = buildProduct(99L, "Ghost", 0, buildCategory(1L, "X"), State.ACTIVE);

            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            Product result = storageProductService.replaceProduct(replacement, 99L);

            assertThat(result).isNull();
            verify(productRepository, never()).save(any());
        }
    }

    // ──── deleteProductById ────

    @Nested
    @DisplayName("deleteProductById")
    class DeleteProductById {

        @Test
        @DisplayName("soft-deletes product by setting state to DELETED")
        void whenProductExists_setsStateToDeletedAndSaves() {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat, State.ACTIVE);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product result = storageProductService.deleteProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getState()).isEqualTo(State.DELETED);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("returns null when product does not exist")
        void whenProductDoesNotExist_returnsNull() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            Product result = storageProductService.deleteProductById(99L);

            assertThat(result).isNull();
            verify(productRepository, never()).save(any());
        }
    }

    // ──── getDetailsBasedOnUserRole ────

    @Nested
    @DisplayName("getDetailsBasedOnUserRole")
    class GetDetailsBasedOnUserRole {

        @Test
        @DisplayName("returns product for admin user regardless of state")
        void whenUserIsAdmin_returnsProduct() {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat, State.INACTIVE);
            UserDto admin = UserDto.builder()
                    .id(10L).email("admin@test.com")
                    .roles(List.of(buildRole("ADMIN")))
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(restTemplate.getForEntity(
                    eq("http://UserAuthenticationService/users/{userId}"),
                    eq(UserDto.class),
                    eq(10L)))
                    .thenReturn(new ResponseEntity<>(admin, HttpStatus.OK));

            Product result = storageProductService.getDetailsBasedOnUserRole(1L, 10L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Phone");
        }

        @Test
        @DisplayName("returns product for non-admin user when product is ACTIVE")
        void whenNonAdminAndProductActive_returnsProduct() {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat, State.ACTIVE);
            UserDto user = UserDto.builder()
                    .id(20L).email("user@test.com")
                    .roles(List.of(buildRole("USER")))
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(restTemplate.getForEntity(anyString(), eq(UserDto.class), eq(20L)))
                    .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

            Product result = storageProductService.getDetailsBasedOnUserRole(1L, 20L);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("returns null for non-admin user when product is INACTIVE")
        void whenNonAdminAndProductInactive_returnsNull() {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat, State.INACTIVE);
            UserDto user = UserDto.builder()
                    .id(20L).email("user@test.com")
                    .roles(List.of(buildRole("USER")))
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(restTemplate.getForEntity(anyString(), eq(UserDto.class), eq(20L)))
                    .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

            Product result = storageProductService.getDetailsBasedOnUserRole(1L, 20L);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("returns null when product does not exist")
        void whenProductDoesNotExist_returnsNull() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            Product result = storageProductService.getDetailsBasedOnUserRole(99L, 10L);

            assertThat(result).isNull();
            verify(restTemplate, never()).getForEntity(anyString(), any(), any(Object.class));
        }

        @Test
        @DisplayName("returns null when user service returns null body")
        void whenUserIsNull_returnsNull() {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat, State.ACTIVE);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(restTemplate.getForEntity(anyString(), eq(UserDto.class), eq(10L)))
                    .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

            Product result = storageProductService.getDetailsBasedOnUserRole(1L, 10L);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("returns null when user has null roles")
        void whenUserRolesAreNull_returnsNull() {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat, State.ACTIVE);
            UserDto user = UserDto.builder()
                    .id(20L).email("user@test.com")
                    .roles(null)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(restTemplate.getForEntity(anyString(), eq(UserDto.class), eq(20L)))
                    .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

            Product result = storageProductService.getDetailsBasedOnUserRole(1L, 20L);

            assertThat(result).isNull();
        }
    }
}

