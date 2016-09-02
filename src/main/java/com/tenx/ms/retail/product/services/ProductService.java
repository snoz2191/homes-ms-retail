package com.tenx.ms.retail.product.services;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repository.ProductRepository;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.product.function.ProductConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductConverter converter;

    @Transactional
    public Long createProduct(Product product) {
        ProductEntity productEntity = converter.convertToProductEntity.apply(product);
        LOGGER.debug("Saving product {} into database", productEntity);
        productRepository.save(productEntity);
        return productEntity.getProductId();
    }

    public Optional<Product> getProductById(Long storeId, Long productId) {
        LOGGER.debug("Trying to retrieve product {} from store {}", productId, storeId);
        return productRepository.findOneByStoreIdAndProductId(storeId, productId).map(converter.convertToProductDTO);
    }

    public List<Product> getAllProducts(Long storeId, Pageable pageable) {
        return productRepository.findAllByStoreId(storeId, pageable).getContent().stream().map(converter.convertToProductDTO).collect(Collectors.toList());
    }

}
