package com.tenx.ms.retail.stock.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel(value = "stock")
public class Stock {
    @ApiModelProperty(value = "Product Id", required = true)
    private Long productId;

    @ApiModelProperty(value = "Store Id", required = true)
    private Long storeId;

    @NotNull
    @Min(value = 0)
    @ApiModelProperty(value = "Total count in stock", required = true)
    private Long count;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
