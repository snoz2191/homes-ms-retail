package com.tenx.ms.retail.order.function;

import com.tenx.ms.retail.order.domain.OrderEntity;
import com.tenx.ms.retail.order.domain.OrderItemEntity;
import com.tenx.ms.retail.order.rest.dto.OrderItem;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class OrderItemConverter {

    public BiFunction<OrderEntity, OrderItem, OrderItemEntity> convertToOrderItemEntity = (OrderEntity orderEntity, OrderItem orderItem) -> {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        if ( orderItem != null ) {
            orderItemEntity.setOrder(orderEntity);
            orderItemEntity.setProductId(orderItem.getProductId());
            orderItemEntity.setCount(orderItem.getCount());
        }
        return orderItemEntity;
    };
}
