package org.ecommerce.productcatalogservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FakeStoreProductDto {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;
}
