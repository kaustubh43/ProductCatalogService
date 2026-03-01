package org.ecommerce.productcatalogservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecommerce.productcatalogservice.dtos.CategoryDto;
import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.exceptions.GlobalExceptionHandler;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.ecommerce.productcatalogservice.services.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ──── Helpers ────

    private Category buildCategory(Long id, String name) {
        return Category.builder().id(id).name(name).description(name + " desc").build();
    }

    private Product buildProduct(Long id, String name, double price, Category category) {
        return Product.builder()
                .id(id).name(name).description(name + " desc")
                .imageUrl("http://img/" + id).price(price)
                .category(category).state(State.ACTIVE)
                .build();
    }

    // ──── GET /products/{id} ────

    @Nested
    @DisplayName("GET /products/{id}")
    class GetProductById {

        @Test
        @DisplayName("returns 200 with product when found")
        void whenValidId_returns200() throws Exception {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599.99, cat);
            when(productService.getProductById(1L)).thenReturn(product);

            mockMvc.perform(get("/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Phone"))
                    .andExpect(jsonPath("$.price").value(599.99))
                    .andExpect(jsonPath("$.categoryDto.name").value("Electronics"));
        }

        @Test
        @DisplayName("returns 400 when id <= 0")
        void whenIdIsZeroOrNegative_returns400() throws Exception {
            mockMvc.perform(get("/products/0"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Product Id must be greater than 0")));
        }

        @Test
        @DisplayName("returns 500 when id > 20")
        void whenIdGreaterThan20_returns500() throws Exception {
            mockMvc.perform(get("/products/21"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Something went wrong")));
        }
    }

    // ──── GET /products ────

    @Nested
    @DisplayName("GET /products")
    class GetAllProducts {

        @Test
        @DisplayName("returns 200 with list of products")
        void returnsAllProducts() throws Exception {
            Category cat = buildCategory(1L, "Books");
            List<Product> products = List.of(
                    buildProduct(1L, "Book A", 10, cat),
                    buildProduct(2L, "Book B", 15, cat)
            );
            when(productService.getAllProducts()).thenReturn(products);

            mockMvc.perform(get("/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Book A"))
                    .andExpect(jsonPath("$[1].name").value("Book B"));
        }

        @Test
        @DisplayName("returns 500 when service throws exception")
        void whenServiceThrows_returns500() throws Exception {
            when(productService.getAllProducts()).thenThrow(new RuntimeException("DB error"));

            mockMvc.perform(get("/products"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ──── POST /products/add ────

    @Nested
    @DisplayName("POST /products/add")
    class AddProduct {

        @Test
        @DisplayName("returns 201 when product created successfully")
        void whenProductCreated_returns201() throws Exception {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat);
            when(productService.createProduct(any(ProductDto.class))).thenReturn(product);

            ProductDto dto = ProductDto.builder()
                    .name("Phone").price(599).description("desc")
                    .imageUrl("http://img/1")
                    .categoryDto(CategoryDto.builder().id(1L).name("Electronics").description("desc").build())
                    .build();

            mockMvc.perform(post("/products/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Phone"));
        }

        @Test
        @DisplayName("returns 500 when product creation fails (null returned)")
        void whenProductCreationFails_returns500() throws Exception {
            when(productService.createProduct(any(ProductDto.class))).thenReturn(null);

            ProductDto dto = ProductDto.builder()
                    .name("Phone").price(599).description("desc")
                    .imageUrl("http://img/1")
                    .categoryDto(CategoryDto.builder().id(1L).name("Electronics").description("desc").build())
                    .build();

            mockMvc.perform(post("/products/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ──── PUT /products/update/{id} ────

    @Nested
    @DisplayName("PUT /products/update/{id}")
    class UpdateProduct {

        @Test
        @DisplayName("returns 200 when product replaced successfully")
        void whenReplaceSucceeds_returns200() throws Exception {
            Category cat = buildCategory(1L, "Electronics");
            Product replaced = buildProduct(1L, "New Phone", 699, cat);
            when(productService.replaceProduct(any(Product.class), eq(1L))).thenReturn(replaced);

            ProductDto dto = ProductDto.builder()
                    .name("New Phone").price(699).description("desc")
                    .imageUrl("http://img/1")
                    .categoryDto(CategoryDto.builder().id(1L).name("Electronics").build())
                    .build();

            mockMvc.perform(put("/products/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New Phone"));
        }

        @Test
        @DisplayName("returns 500 when replace throws exception")
        void whenReplaceThrows_returns500() throws Exception {
            when(productService.replaceProduct(any(Product.class), eq(1L)))
                    .thenThrow(new RuntimeException("fail"));

            ProductDto dto = ProductDto.builder()
                    .name("X").price(10).description("d")
                    .imageUrl("http://img/1")
                    .categoryDto(CategoryDto.builder().id(1L).name("Cat").build())
                    .build();

            mockMvc.perform(put("/products/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ──── PATCH /products/patch/{id} ────

    @Test
    @DisplayName("PATCH /products/patch/{id} returns 500 (not implemented)")
    void patchProduct_returns500() throws Exception {
        ProductDto dto = ProductDto.builder()
                .name("X").price(10).description("d")
                .imageUrl("http://img/1")
                .categoryDto(CategoryDto.builder().id(1L).name("Cat").build())
                .build();

        mockMvc.perform(patch("/products/patch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    // ──── DELETE /products/{id} ────

    @Nested
    @DisplayName("DELETE /products/{id}")
    class DeleteProduct {

        @Test
        @DisplayName("returns 200 when product deleted")
        void whenDeleteSucceeds_returns200() throws Exception {
            Category cat = buildCategory(1L, "Electronics");
            Product deleted = buildProduct(1L, "Phone", 599, cat);
            when(productService.deleteProductById(1L)).thenReturn(deleted);

            mockMvc.perform(delete("/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Phone"));
        }

        @Test
        @DisplayName("returns 500 when delete returns null")
        void whenDeleteReturnsNull_returns500() throws Exception {
            when(productService.deleteProductById(99L)).thenReturn(null);

            mockMvc.perform(delete("/products/99"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ──── GET /products/{productId}/{userId} ────

    @Nested
    @DisplayName("GET /products/{productId}/{userId}")
    class GetProductBasedOnUserRole {

        @Test
        @DisplayName("returns product when user has access")
        void whenAuthorized_returnsProduct() throws Exception {
            Category cat = buildCategory(1L, "Electronics");
            Product product = buildProduct(1L, "Phone", 599, cat);
            when(productService.getDetailsBasedOnUserRole(1L, 10L)).thenReturn(product);

            mockMvc.perform(get("/products/1/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Phone"));
        }

        @Test
        @DisplayName("returns 500 when product not found or not authorized")
        void whenNotAuthorized_returns500() throws Exception {
            when(productService.getDetailsBasedOnUserRole(1L, 10L)).thenReturn(null);

            mockMvc.perform(get("/products/1/10"))
                    .andExpect(status().isInternalServerError());
        }
    }
}

