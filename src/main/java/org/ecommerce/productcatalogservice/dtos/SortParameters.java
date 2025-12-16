package org.ecommerce.productcatalogservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortParameters {
    private String sortCriteria;
    private SortOrder sortOrder;
}
