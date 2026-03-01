package org.ecommerce.productcatalogservice.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StateTest {

    @Test
    @DisplayName("State enum has exactly 4 values")
    void stateEnum_hasFourValues() {
        assertThat(State.values()).hasSize(4);
    }

    @Test
    @DisplayName("State enum contains ACTIVE")
    void stateEnum_containsActive() {
        assertThat(State.valueOf("ACTIVE")).isEqualTo(State.ACTIVE);
    }

    @Test
    @DisplayName("State enum contains INACTIVE")
    void stateEnum_containsInactive() {
        assertThat(State.valueOf("INACTIVE")).isEqualTo(State.INACTIVE);
    }

    @Test
    @DisplayName("State enum contains DELETED")
    void stateEnum_containsDeleted() {
        assertThat(State.valueOf("DELETED")).isEqualTo(State.DELETED);
    }

    @Test
    @DisplayName("State enum contains OUT_OF_STOCK")
    void stateEnum_containsOutOfStock() {
        assertThat(State.valueOf("OUT_OF_STOCK")).isEqualTo(State.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("State.valueOf throws for invalid name")
    void stateEnum_throwsForInvalidName() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> State.valueOf("UNKNOWN"));
    }
}

