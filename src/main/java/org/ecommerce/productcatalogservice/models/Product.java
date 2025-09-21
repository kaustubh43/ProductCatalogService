package org.ecommerce.productcatalogservice.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Product extends BaseModel {
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private Category category;

    // Business Specific Fields
    private Boolean isPrime;
}
