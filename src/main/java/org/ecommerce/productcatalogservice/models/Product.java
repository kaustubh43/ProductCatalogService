package org.ecommerce.productcatalogservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
public class Product extends BaseModel {
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;

    // Business Specific Fields
    private Boolean isPrime;
}
