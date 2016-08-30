package com.tenx.ms.retail.stock.repositories;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.stock.domain.StockEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends CrudRepository<StockEntity, Long>{
    Optional<StockEntity> findOneByProduct(ProductEntity product);
}
