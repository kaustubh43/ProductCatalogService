package org.ecommerce.productcatalogservice.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecommerce.productcatalogservice.exceptions.GlobalExceptionHandler;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.services.ICategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private ICategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Mixin to override the broken @JsonBackReference on Category.products
     * so Jackson can deserialize Category from JSON in tests.
     */
    abstract static class CategoryMixin {
        @JsonIgnore
        List<Product> products;
    }

    @BeforeEach
    void setUp() {
        ObjectMapper customMapper = new ObjectMapper();
        customMapper.addMixIn(Category.class, CategoryMixin.class);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(customMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setMessageConverters(converter)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Category buildCategory(Long id, String name, String description) {
        Category cat = new Category();
        cat.setId(id);
        cat.setName(name);
        cat.setDescription(description);
        return cat;
    }

    // ──── GET /category/{id} ────

    @Nested
    @DisplayName("GET /category/{id}")
    class GetCategoryById {

        @Test
        @DisplayName("returns 200 with category when found")
        void whenCategoryExists_returns200() throws Exception {
            Category cat = buildCategory(1L, "Electronics", "Electronic items");
            when(categoryService.getCategoryById(1L)).thenReturn(cat);

            mockMvc.perform(get("/category/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Electronics"))
                    .andExpect(jsonPath("$.description").value("Electronic items"));
        }

        @Test
        @DisplayName("returns 500 when category not found (null)")
        void whenCategoryNotFound_returns500() throws Exception {
            when(categoryService.getCategoryById(99L)).thenReturn(null);

            mockMvc.perform(get("/category/99"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Category could not be found")));
        }
    }

    // ──── POST /category ────

    @Nested
    @DisplayName("POST /category")
    class CreateCategory {

        @Test
        @DisplayName("returns 201 when category created successfully")
        void whenCreated_returns201() throws Exception {
            Category cat = buildCategory(1L, "Books", "All books");
            when(categoryService.createCategory("Books", "All books")).thenReturn(cat);

            String json = "{\"name\":\"Books\",\"description\":\"All books\"}";

            mockMvc.perform(post("/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Books"));
        }

        @Test
        @DisplayName("returns 500 when creation fails (null)")
        void whenCreationFails_returns500() throws Exception {
            when(categoryService.createCategory("Ghost", "desc")).thenReturn(null);

            String json = "{\"name\":\"Ghost\",\"description\":\"desc\"}";

            mockMvc.perform(post("/category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ──── PUT /category/update ────

    @Test
    @DisplayName("PUT /category/update returns 500 (not implemented)")
    void updateCategory_returns500() throws Exception {
        String json = "{\"name\":\"X\"}";

        mockMvc.perform(put("/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError());
    }

    // ──── PATCH /category/patch ────

    @Test
    @DisplayName("PATCH /category/patch returns 500 (not implemented)")
    void patchCategory_returns500() throws Exception {
        String json = "{\"name\":\"X\"}";

        mockMvc.perform(patch("/category/patch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError());
    }

    // ──── DELETE /category/delete ────

    @Test
    @DisplayName("DELETE /category/delete returns 500 (not implemented)")
    void deleteCategory_returns500() throws Exception {
        String json = "{\"name\":\"X\"}";

        mockMvc.perform(delete("/category/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError());
    }

    // ──── GET /category ────

    @Nested
    @DisplayName("GET /category")
    class GetAllCategories {

        @Test
        @DisplayName("returns 200 with list of categories")
        void returnsCategoryList() throws Exception {
            List<Category> cats = List.of(
                    buildCategory(1L, "Electronics", "desc1"),
                    buildCategory(2L, "Books", "desc2")
            );
            when(categoryService.getCategories()).thenReturn(cats);

            mockMvc.perform(get("/category"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Electronics"))
                    .andExpect(jsonPath("$[1].name").value("Books"));
        }

        @Test
        @DisplayName("returns 500 when categories list is null")
        void whenNull_returns500() throws Exception {
            when(categoryService.getCategories()).thenReturn(null);

            mockMvc.perform(get("/category"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("returns 500 when categories list is empty")
        void whenEmpty_returns500() throws Exception {
            when(categoryService.getCategories()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/category"))
                    .andExpect(status().isInternalServerError());
        }
    }
}

