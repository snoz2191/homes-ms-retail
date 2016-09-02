package com.tenx.ms.retail.order.domain.enums;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Order Status")
public enum OrderStatusEnum {
    ORDERED,
    PACKING,
    SHIPPING
}
