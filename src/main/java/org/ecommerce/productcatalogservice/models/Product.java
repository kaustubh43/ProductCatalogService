package org.ecommerce.productcatalogservice.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Product extends BaseModel {
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private Category category;

    // Business Specific Fields
    private Boolean isPrime;

    public Product() {
        this.setCreatedAt(new Date());
        this.setLastUpdatedAt(new Date());
        this.setState(State.ACTIVE);
    }
}
