package com.tenx.ms.retail.store.rest.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("Store")
public class Store {

    @ApiModelProperty(value = "The store ID", readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "The store name", required = true)
    @NotNull
    private String name;

    public Store() {
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
