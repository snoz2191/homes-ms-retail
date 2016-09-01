package com.tenx.ms.retail.order.rest.dto;

import com.tenx.ms.commons.validation.constraints.Email;
import com.tenx.ms.commons.validation.constraints.PhoneNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel("Order")
public class Order {

    @ApiModelProperty(value = "Order Id", readOnly = true)
    private Long orderId;

    @ApiModelProperty(value = "Store Id", readOnly = true)
    private Long storeId;

    @ApiModelProperty(value = "Order Date", readOnly = true)
    private LocalDateTime orderDate;

    @ApiModelProperty(value = "Order Status", readOnly = true)
    private String status;

    @Valid
    @NotNull
    @Size(min = 1)
    @ApiModelProperty(value = "Order Items", required = true)
    private List<OrderItem> products;

    @NotNull
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only alpha characters")
    @ApiModelProperty(value = "Client's First Name", required = true)
    private String firstName;

    @NotNull
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only alpha characters")
    @ApiModelProperty(value = "Client's Last Name", required = true)
    private String lastName;

    @NotNull
    @Email
    @ApiModelProperty(value = "Client' Email")
    private String email;

    @NotNull
    @PhoneNumber
    @ApiModelProperty(value = "Client's Phone Number")
    private String phone;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getProducts() {
        return products;
    }

    public void setProducts(List<OrderItem> products) {
        this.products = products;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
