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

    @ApiModelProperty(value = "The product name", required = true)
    @NotNull
    private String name;

    @ApiModelProperty(value = "The product description")
    @NotNull
    private String description;

    @ApiModelProperty(value = "The product stock unit")
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "SKU must be alphanumeric")
    @Length.List({
            @Length(min = 5, message = "SKU must be at least 5 characters"),
            @Length(max = 10, message = "SKU must be less than 10 characters"),
    })
    private String sku;

    @ApiModelProperty(value = "The product price", required = true)
    @NotNull
    @DollarAmount
    private BigDecimal price;

    public Product() {
    }

    public Product(Long productId, Long storeId, String name, String description, String sku, BigDecimal price) {
        this.productId = productId;
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
    }

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
