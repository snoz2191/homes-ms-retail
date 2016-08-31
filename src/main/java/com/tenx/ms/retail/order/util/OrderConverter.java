package com.tenx.ms.retail.order.util;

import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.rest.dto.Order;
import com.tenx.ms.retail.store.domain.StoreEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderConverter {

    public OrderEntity convertToOrderEntity (Order order, StoreEntity storeEntity) {
        OrderEntity orderEntity = new OrderEntity();
        if ( order != null ) {
            orderEntity.setStore(storeEntity);
            orderEntity.setFirstName(order.getFirstName());
            orderEntity.setLastName(order.getLastName());
            orderEntity.setEmail(order.getEmail());
            orderEntity.setPhone(order.getPhone());
            if ( order.getStatus() != null ) {
                orderEntity.setStatus(order.getStatus());
            }
            if ( order.getOrderDate() != null ) {
                orderEntity.setOrderDate(order.getOrderDate());
            } else {
                orderEntity.setOrderDate(LocalDateTime.now());
            }
        }
        return orderEntity;
    }
}
