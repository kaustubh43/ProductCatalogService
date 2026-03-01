package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.dtos.SortOrder;
import org.ecommerce.productcatalogservice.dtos.SortParameters;
import org.ecommerce.productcatalogservice.models.Category;
import org.ecommerce.productcatalogservice.models.Product;
import org.ecommerce.productcatalogservice.models.State;
import org.ecommerce.productcatalogservice.repositories.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageSearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StorageSearchService storageSearchService;

    private SortParameters buildSortParam(String criteria, SortOrder order) {
        SortParameters sp = new SortParameters();
        sp.setSortCriteria(criteria);
        sp.setSortOrder(order);
        return sp;
    }

    private Product buildProduct(Long id, String name, double price) {
        return Product.builder()
                .id(id).name(name).price(price)
                .category(Category.builder().name("Test").build())
                .state(State.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("searches with single ascending sort parameter")
    void searchWithSingleAscendingSort() {
        List<SortParameters> sorts = List.of(buildSortParam("price", SortOrder.ASCENDING));
        Page<Product> page = new PageImpl<>(List.of(buildProduct(1L, "A", 10)));
        when(productRepository.findAllByName(eq("phone"), any(PageRequest.class))).thenReturn(page);

        Page<Product> result = storageSearchService.searchProducts("phone", 5, 0, sorts);

        assertThat(result.getContent()).hasSize(1);

        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(productRepository).findAllByName(eq("phone"), captor.capture());
        PageRequest captured = captor.getValue();
        assertThat(captured.getPageNumber()).isZero();
        assertThat(captured.getPageSize()).isEqualTo(5);
        assertThat(captured.getSort().getOrderFor("price")).isNotNull();
        assertThat(captured.getSort().getOrderFor("price").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("searches with single descending sort parameter")
    void searchWithSingleDescendingSort() {
        List<SortParameters> sorts = List.of(buildSortParam("price", SortOrder.DESCENDING));
        Page<Product> page = new PageImpl<>(List.of(buildProduct(1L, "B", 20)));
        when(productRepository.findAllByName(eq("laptop"), any(PageRequest.class))).thenReturn(page);

        Page<Product> result = storageSearchService.searchProducts("laptop", 10, 0, sorts);

        assertThat(result.getContent()).hasSize(1);

        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(productRepository).findAllByName(eq("laptop"), captor.capture());
        assertThat(captor.getValue().getSort().getOrderFor("price").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("searches with multiple sort parameters")
    void searchWithMultipleSortParameters() {
        List<SortParameters> sorts = List.of(
                buildSortParam("price", SortOrder.ASCENDING),
                buildSortParam("id", SortOrder.DESCENDING)
        );
        Page<Product> page = new PageImpl<>(List.of());
        when(productRepository.findAllByName(eq("tablet"), any(PageRequest.class))).thenReturn(page);

        storageSearchService.searchProducts("tablet", 4, 1, sorts);

        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(productRepository).findAllByName(eq("tablet"), captor.capture());
        Sort sort = captor.getValue().getSort();
        assertThat(sort.getOrderFor("price").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(sort.getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(captor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(captor.getValue().getPageSize()).isEqualTo(4);
    }
}

