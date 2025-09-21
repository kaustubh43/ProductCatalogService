package org.ecommerce.productcatalogservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private CategoryDto categoryDto;
}
