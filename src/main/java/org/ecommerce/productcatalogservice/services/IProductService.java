package org.ecommerce.productcatalogservice.services;

import org.ecommerce.productcatalogservice.dtos.ProductDto;
import org.ecommerce.productcatalogservice.models.Product;

import java.util.List;

public interface IProductService {

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product createProduct(ProductDto productDto);

    Product replaceProduct(Product product, Long id);

    Product deleteProductById(Long id);
}
