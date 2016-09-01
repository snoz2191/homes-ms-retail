package com.tenx.ms.retail.product.util;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.rest.dto.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {
    public ProductEntity convertToProductEntity(Product product) {
        ProductEntity productEntity = new ProductEntity();
        if ( product != null ) {
            productEntity.setStoreId(product.getStoreId());
            productEntity.setName(product.getName());
            productEntity.setDescription(product.getDescription());
            productEntity.setSku(product.getSku());
            productEntity.setPrice(product.getPrice());
        }
        return  productEntity;
    }

    public Product convertToProductDTO(ProductEntity productEntity) {
        Product product = new Product();
        if ( productEntity != null ) {
            product.setProductId(productEntity.getProductId());
            product.setStoreId(productEntity.getStoreId());
            product.setName(productEntity.getName());
            product.setDescription(productEntity.getDescription());
            product.setSku(productEntity.getSku());
            product.setPrice(productEntity.getPrice());
        }
        return product;
    }
}
