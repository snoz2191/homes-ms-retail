package com.tenx.ms.retail.product.repositories;

import com.tenx.ms.retail.product.domain.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, Long> {
    Optional<ProductEntity> findOneByStoreIdAndProductId(final Long storeId, final Long productId);

    Page<ProductEntity> findAllByStoreId(final Long storeId, Pageable pageable);
}
