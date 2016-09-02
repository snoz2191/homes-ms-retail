package com.tenx.ms.retail.order.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "Order Item Description")
public class OrderItem {

    @NotNull
    @ApiModelProperty(value = "Ordered Product Id", required = true)
    private Long productId;

    @NotNull
    @ApiModelProperty(value = "Ordered Product Quantity", required = true)
    private Long count;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
