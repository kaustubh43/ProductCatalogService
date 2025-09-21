package org.ecommerce.productcatalogservice.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class BaseModel {
    private long id;
    private Date createdAt;
    private Date lastUpdatedAt;
    private State state;
}
