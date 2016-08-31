package com.tenx.ms.retail.stock.services;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repositories.ProductRepository;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.repositories.StockRepository;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import com.tenx.ms.retail.stock.util.StockConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StockService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockConverter converter;

    @Transactional
    public void upsertStock(Long storeId, Long productId, Stock stock) {
        StockEntity upsertStock;
        Optional<ProductEntity> product = productRepository.findOneByStoreIdAndProductId(storeId, productId);
        if ( product.isPresent() ) {
            Optional<StockEntity> stockEntity = stockRepository.findOneByProduct(product.get());
            if ( stockEntity.isPresent() ) {
                upsertStock = stockEntity.get();
                upsertStock.setCount(stock.getCount());
            } else {
                upsertStock = converter.convertToStockEntity(product.get(), stock);
            }
            LOGGER.debug("Saving stock for store: {} and product: {}", storeId, productId);
            stockRepository.save(upsertStock);
        } else {
            throw new NoSuchElementException(String.format("There is no product %d for store %d", productId, storeId));
        }
    }

    public Stock findStockByProduct(ProductEntity product) {
        Optional<StockEntity> stockEntity = stockRepository.findOneByProduct(product);
        if (stockEntity.isPresent()) {
            return converter.convertToStockDTO(stockEntity.get());
        } else {
            Stock stock = new Stock();
            stock.setProductId(product.getProductId());
            stock.setStoreId(product.getStoreId());
            stock.setCount(0L);
            return stock;
        }
    }
}
