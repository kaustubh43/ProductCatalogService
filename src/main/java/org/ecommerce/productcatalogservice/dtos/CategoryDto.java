package org.ecommerce.productcatalogservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}
