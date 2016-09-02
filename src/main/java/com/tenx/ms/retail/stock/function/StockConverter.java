package com.tenx.ms.retail.stock.function;

import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.stock.domain.StockEntity;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class StockConverter {
    public BiFunction<ProductEntity, Stock, StockEntity> convertToStockEntity = (ProductEntity productEntity, Stock stock) -> {
        StockEntity stockEntity = new StockEntity();
        if ( stock != null ) {
            stockEntity.setProduct(productEntity);
            stockEntity.setCount(stock.getCount());
        }
        return  stockEntity;
    };

    public Function<StockEntity, Stock> convertToStockDTO = (StockEntity stockEntity) -> {
        Stock stock = new Stock();
        if ( stockEntity != null ) {
            stock.setStoreId(stockEntity.getProduct().getStoreId());
            stock.setProductId(stockEntity.getProduct().getProductId());
            stock.setCount(stockEntity.getCount());
        }
        return stock;
    };
}
