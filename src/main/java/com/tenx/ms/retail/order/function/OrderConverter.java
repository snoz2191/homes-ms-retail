package com.tenx.ms.retail.order.function;

import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.rest.dto.Order;
import com.tenx.ms.retail.store.domain.StoreEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

@Component
public class OrderConverter {

    public BiFunction<Order, StoreEntity, OrderEntity> convertToOrderEntity = (Order order, StoreEntity storeEntity) -> {
        OrderEntity orderEntity = new OrderEntity();
        if ( order != null ) {
            orderEntity.setStore(storeEntity);
            orderEntity.setFirstName(order.getFirstName());
            orderEntity.setLastName(order.getLastName());
            orderEntity.setEmail(order.getEmail());
            orderEntity.setPhone(order.getPhone());
            orderEntity.setOrderDate(LocalDateTime.now());
        }
        return orderEntity;
    };
}
