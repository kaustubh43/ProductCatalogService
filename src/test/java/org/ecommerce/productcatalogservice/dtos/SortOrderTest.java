package org.ecommerce.productcatalogservice.dtos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SortOrderTest {

    @Test
    @DisplayName("SortOrder enum has exactly 2 values")
    void sortOrderEnum_hasTwoValues() {
        assertThat(SortOrder.values()).hasSize(2);
    }

    @Test
    @DisplayName("SortOrder enum contains ASCENDING")
    void sortOrderEnum_containsAscending() {
        assertThat(SortOrder.valueOf("ASCENDING")).isEqualTo(SortOrder.ASCENDING);
    }

    @Test
    @DisplayName("SortOrder enum contains DESCENDING")
    void sortOrderEnum_containsDescending() {
        assertThat(SortOrder.valueOf("DESCENDING")).isEqualTo(SortOrder.DESCENDING);
    }
}

