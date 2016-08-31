package com.tenx.ms.retail.product.rest.dto;

import com.tenx.ms.commons.validation.constraints.DollarAmount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@ApiModel("Product")
public class Product {

    @ApiModelProperty(value = "The product ID", readOnly = true)
    private Long productId;

    @ApiModelProperty(value = "The store ID for which the product belongs", readOnly = true)
    private Long storeId;

    @NotNull
    @ApiModelProperty(value = "The product name", required = true)
    private String name;

    @NotNull
    @ApiModelProperty(value = "The product description")
    private String description;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "SKU must be alphanumeric")
    @Length.List({
            @Length(min = 5, message = "SKU must be at least 5 characters"),
            @Length(max = 10, message = "SKU must be less than 10 characters"),
    })
    @ApiModelProperty(value = "The product stock unit")
    private String sku;

    @NotNull
    @DollarAmount
    @ApiModelProperty(value = "The product price", required = true)
    private BigDecimal price;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
