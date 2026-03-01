package org.ecommerce.productcatalogservice.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    @DisplayName("equalsIgnoreCase returns true for matching value (case-insensitive)")
    void equalsIgnoreCase_matchingValue_returnsTrue() {
        Role role = new Role();
        role.setValue("ADMIN");

        assertThat(role.equalsIgnoreCase("admin")).isTrue();
        assertThat(role.equalsIgnoreCase("Admin")).isTrue();
        assertThat(role.equalsIgnoreCase("ADMIN")).isTrue();
    }

    @Test
    @DisplayName("equalsIgnoreCase returns false for non-matching value")
    void equalsIgnoreCase_nonMatchingValue_returnsFalse() {
        Role role = new Role();
        role.setValue("USER");

        assertThat(role.equalsIgnoreCase("ADMIN")).isFalse();
    }

    @Test
    @DisplayName("equalsIgnoreCase returns false when value is null")
    void equalsIgnoreCase_nullValue_returnsFalse() {
        Role role = new Role();
        // value is null by default

        assertThat(role.equalsIgnoreCase("ADMIN")).isFalse();
    }

    @Test
    @DisplayName("equalsIgnoreCase returns false when argument is null")
    void equalsIgnoreCase_nullArgument_returnsFalse() {
        Role role = new Role();
        role.setValue("ADMIN");

        assertThat(role.equalsIgnoreCase(null)).isFalse();
    }

    @Test
    @DisplayName("equalsIgnoreCase returns false when both are null")
    void equalsIgnoreCase_bothNull_returnsFalse() {
        Role role = new Role();
        // value is null

        assertThat(role.equalsIgnoreCase(null)).isFalse();
    }

    @Test
    @DisplayName("getter and setter work correctly")
    void getterSetter_worksCorrectly() {
        Role role = new Role();
        role.setValue("MODERATOR");

        assertThat(role.getValue()).isEqualTo("MODERATOR");
    }
}

