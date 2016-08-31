package com.tenx.ms.retail.order.util;

import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.domain.OrderItemEntity;
import com.tenx.ms.retail.order.rest.dto.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {

    public OrderItemEntity convertToOrderItemEntity(OrderEntity orderEntity, OrderItem orderItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        if ( orderItem != null ) {
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setProductId(orderItem.getProductId());
            orderItemEntity.setCount(orderItem.getCount());
        }
        return orderItemEntity;
    }
}
