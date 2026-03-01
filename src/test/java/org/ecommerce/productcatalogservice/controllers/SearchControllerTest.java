package org.ecommerce.productcatalogservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecommerce.productcatalogservice.dtos.SearchRequestDto;
import org.ecommerce.productcatalogservice.dtos.SortOrder;
import org.ecommerce.productcatalogservice.dtos.SortParameters;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.ecommerce.productcatalogservice.services.ISearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private ISearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    @DisplayName("POST /search delegates to search service and returns 200")
    void searchProducts_returnsPaginatedResults() throws Exception {
        Product product = Product.builder()
                .id(1L).name("Galaxy phone").price(999)
                .description("desc").imageUrl("http://img/1")
                .category(Category.builder().id(1L).name("Electronics").build())
                .state(State.ACTIVE)
                .build();
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Page<Product> page = new PageImpl<>(productList, PageRequest.of(0, 4), 1);

        when(searchService.searchProducts(eq("Galaxy phone"), eq(4), eq(0), anyList()))
                .thenReturn(page);

        SearchRequestDto request = new SearchRequestDto();
        request.setQuery("Galaxy phone");
        request.setPageSize(4);
        request.setPageNumber(0);

        SortParameters sp = new SortParameters();
        sp.setSortCriteria("price");
        sp.setSortOrder(SortOrder.ASCENDING);
        request.setSortParameters(List.of(sp));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(searchService).searchProducts(eq("Galaxy phone"), eq(4), eq(0), anyList());
    }

    @Test
    @DisplayName("POST /search returns 200 for empty results")
    void searchProducts_returnsEmptyPage() throws Exception {
        Page<Product> emptyPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);
        when(searchService.searchProducts(anyString(), anyInt(), anyInt(), anyList()))
                .thenReturn(emptyPage);

        SearchRequestDto request = new SearchRequestDto();
        request.setQuery("nonexistent");
        request.setPageSize(10);
        request.setPageNumber(0);

        SortParameters sp = new SortParameters();
        sp.setSortCriteria("price");
        sp.setSortOrder(SortOrder.ASCENDING);
        request.setSortParameters(List.of(sp));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

