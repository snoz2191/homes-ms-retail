package com.tenx.ms.retail.stock.domain;

import com.tenx.ms.retail.product.domain.ProductEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "stock")
public class StockEntity {

    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long stockId;

    @OneToOne(targetEntity = ProductEntity.class)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @NotNull
    private ProductEntity product;

    @Column(name = "stock_count")
    @NotNull
    private Long count;

    public StockEntity() {
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
