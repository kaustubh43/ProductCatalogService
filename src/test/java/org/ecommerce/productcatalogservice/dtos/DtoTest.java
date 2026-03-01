package org.ecommerce.productcatalogservice.dtos;

import org.ecommerce.productcatalogservice.models.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Nested
    @DisplayName("ProductDto")
    class ProductDtoTest {

        @Test
        @DisplayName("builder creates ProductDto with all fields")
        void builder_createsWithAllFields() {
            CategoryDto categoryDto = CategoryDto.builder()
                    .id(1L).name("Electronics").description("desc").build();

            ProductDto dto = ProductDto.builder()
                    .id(1L).name("Phone").description("Smart phone")
                    .imageUrl("http://img/1").price(599.99)
                    .categoryDto(categoryDto)
                    .build();

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Phone");
            assertThat(dto.getDescription()).isEqualTo("Smart phone");
            assertThat(dto.getImageUrl()).isEqualTo("http://img/1");
            assertThat(dto.getPrice()).isEqualTo(599.99);
            assertThat(dto.getCategoryDto()).isNotNull();
            assertThat(dto.getCategoryDto().getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("setters update ProductDto fields")
        void setters_updateFields() {
            ProductDto dto = ProductDto.builder()
                    .id(1L).name("Old").description("old desc")
                    .imageUrl("http://old").price(100)
                    .categoryDto(CategoryDto.builder().id(1L).name("A").build())
                    .build();

            dto.setName("New");
            dto.setPrice(200);

            assertThat(dto.getName()).isEqualTo("New");
            assertThat(dto.getPrice()).isEqualTo(200);
        }
    }

    @Nested
    @DisplayName("CategoryDto")
    class CategoryDtoTest {

        @Test
        @DisplayName("builder creates CategoryDto with all fields")
        void builder_createsWithAllFields() {
            CategoryDto dto = CategoryDto.builder()
                    .id(1L).name("Books").description("All books").build();

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Books");
            assertThat(dto.getDescription()).isEqualTo("All books");
        }
    }

    @Nested
    @DisplayName("FakeStoreProductDto")
    class FakeStoreProductDtoTest {

        @Test
        @DisplayName("builder creates FakeStoreProductDto with all fields")
        void builder_createsWithAllFields() {
            FakeStoreProductDto dto = FakeStoreProductDto.builder()
                    .id(1L).title("Phone").price(599.0)
                    .description("desc").category("Electronics")
                    .image("http://img/1")
                    .build();

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getTitle()).isEqualTo("Phone");
            assertThat(dto.getPrice()).isEqualTo(599.0);
            assertThat(dto.getDescription()).isEqualTo("desc");
            assertThat(dto.getCategory()).isEqualTo("Electronics");
            assertThat(dto.getImage()).isEqualTo("http://img/1");
        }
    }

    @Nested
    @DisplayName("SearchRequestDto")
    class SearchRequestDtoTest {

        @Test
        @DisplayName("getters and setters work correctly")
        void gettersSetters_workCorrectly() {
            SortParameters sp = new SortParameters();
            sp.setSortCriteria("price");
            sp.setSortOrder(SortOrder.ASCENDING);

            SearchRequestDto dto = new SearchRequestDto();
            dto.setQuery("phone");
            dto.setPageSize(10);
            dto.setPageNumber(0);
            dto.setSortParameters(List.of(sp));

            assertThat(dto.getQuery()).isEqualTo("phone");
            assertThat(dto.getPageSize()).isEqualTo(10);
            assertThat(dto.getPageNumber()).isZero();
            assertThat(dto.getSortParameters()).hasSize(1);
            assertThat(dto.getSortParameters().get(0).getSortCriteria()).isEqualTo("price");
        }
    }

    @Nested
    @DisplayName("SortParameters")
    class SortParametersTest {

        @Test
        @DisplayName("getters and setters work correctly")
        void gettersSetters_workCorrectly() {
            SortParameters sp = new SortParameters();
            sp.setSortCriteria("name");
            sp.setSortOrder(SortOrder.DESCENDING);

            assertThat(sp.getSortCriteria()).isEqualTo("name");
            assertThat(sp.getSortOrder()).isEqualTo(SortOrder.DESCENDING);
        }
    }

    @Nested
    @DisplayName("UserDto")
    class UserDtoTest {

        @Test
        @DisplayName("builder creates UserDto with all fields")
        void builder_createsWithAllFields() {
            Role role = new Role();
            role.setValue("ADMIN");

            UserDto dto = UserDto.builder()
                    .id(1L).email("test@test.com")
                    .name("Test User").phoneNumber("1234567890")
                    .roles(List.of(role))
                    .build();

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getEmail()).isEqualTo("test@test.com");
            assertThat(dto.getName()).isEqualTo("Test User");
            assertThat(dto.getPhoneNumber()).isEqualTo("1234567890");
            assertThat(dto.getRoles()).hasSize(1);
            assertThat(dto.getRoles().get(0).getValue()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("no-args constructor creates empty UserDto")
        void noArgsConstructor_createsEmptyDto() {
            UserDto dto = new UserDto();

            assertThat(dto.getId()).isNull();
            assertThat(dto.getEmail()).isNull();
            assertThat(dto.getName()).isNull();
            assertThat(dto.getRoles()).isNull();
        }
    }
}

