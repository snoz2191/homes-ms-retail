package com.tenx.ms.retail.order.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.avro.reflect.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("OrderResponse")
public class OrderResponse {

    @NotNull
    @ApiModelProperty(value = "Order Id", required = true)
    private Long orderId;

    @NotNull
    @ApiModelProperty(value = "Order Status", readOnly = true)
    private String status;

    @Nullable
    @ApiModelProperty(value = "Backdorder Items", required = true)
    private List<OrderItem> backorderedItems;

    public OrderResponse() {
    }

    public OrderResponse(Long orderId, String status, List<OrderItem> backorderedItems) {
        this.orderId = orderId;
        this.status = status;
        this.backorderedItems = backorderedItems;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getBackorderedItems() {
        return backorderedItems;
    }

    public void setBackorderedItems(List<OrderItem> backorderedItems) {
        this.backorderedItems = backorderedItems;
    }
}
