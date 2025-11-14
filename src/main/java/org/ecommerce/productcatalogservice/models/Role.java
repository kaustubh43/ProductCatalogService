package org.ecommerce.productcatalogservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {
    private String value;

    public boolean equalsIgnoreCase(String roleValue) {
        return this.value != null && this.value.equalsIgnoreCase(roleValue);
    }
}
