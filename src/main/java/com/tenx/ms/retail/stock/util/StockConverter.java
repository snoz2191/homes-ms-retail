package com.tenx.ms.retail.stock.util;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockConverter {
    public StockEntity convertToStockEntity(ProductEntity productEntity, Stock stock) {
        StockEntity stockEntity = new StockEntity();
        if ( stock != null ) {
            stockEntity.setProduct(productEntity);
            stockEntity.setCount(stock.getCount());
        }
        return  stockEntity;
    }

    public Stock convertToStockDTO(StockEntity stockEntity) {
        Stock stock = new Stock();
        if ( stockEntity != null ) {
            stock.setStoreId(stockEntity.getProduct().getStoreId());
            stock.setProductId(stockEntity.getProduct().getProductId());
            stock.setCount(stockEntity.getCount());
        }
        return stock;
    }
}
