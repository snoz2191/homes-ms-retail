package com.tenx.ms.retail.order.services;

import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.domain.OrderItemEntity;
import com.tenx.ms.retail.order.repositories.OrderItemRepository;
import com.tenx.ms.retail.order.repositories.OrderRepository;
import com.tenx.ms.retail.order.rest.dto.Order;
import com.tenx.ms.retail.order.rest.dto.OrderItem;
import com.tenx.ms.retail.order.rest.dto.OrderResponse;
import com.tenx.ms.retail.order.util.OrderConverter;
import com.tenx.ms.retail.order.util.OrderItemConverter;
import com.tenx.ms.retail.order.domain.enums.OrderStatusEnum;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.repositories.ProductRepository;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import com.tenx.ms.retail.stock.services.StockService;
import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repositories.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderConverter orderConverter;

    @Autowired
    private OrderItemConverter orderItemConverter;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder (Order order) {
        Optional<StoreEntity> optionalStore = storeRepository.findOneByStoreId(order.getStoreId());
        if ( !optionalStore.isPresent() ) {
            throw new NoSuchElementException(String.format("Store with id %d was not found", order.getStoreId()));
        }

        OrderEntity orderEntity = orderConverter.convertToOrderEntity(order, optionalStore.get());
        orderEntity.setStatus(OrderStatusEnum.ORDERED.toString());

        List<OrderItem> backorderedItems = new ArrayList<>();

        for (OrderItem orderItem : order.getProducts()) {
            Optional<ProductEntity> product = productRepository.findOneByStoreIdAndProductId(order.getStoreId(), orderItem.getProductId());
            if ( !product.isPresent() ) {
                throw new NoSuchElementException(String.format("Product with id %d was not found", orderItem.getProductId()));
            }

            Stock stock = stockService.findStockByProduct(product.get());
            if (stock.getCount() >= orderItem.getCount()) {
                stock.setCount(stock.getCount() - orderItem.getCount());
                stockService.upsertStock(order.getStoreId(), orderItem.getProductId(), stock);
            } else {
                backorderedItems.add(orderItem);
            }
        }

        OrderEntity result = orderRepository.save(orderEntity);
        List<OrderItemEntity> items = order.getProducts().stream().map(item -> orderItemConverter.convertToOrderItemEntity(result, item)).collect(Collectors.toList());
        orderItemRepository.save(items);
        return new OrderResponse(result.getOrderId(), result.getStatus(), backorderedItems);
    }
}
