CREATE TABLE store (
    store_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'The id of the store',
    name VARCHAR(50) NOT NULL COMMENT 'The name of the store',
    PRIMARY KEY (store_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;

CREATE TABLE product (
    product_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'The id of the product',
    store_id BIGINT NOT NULL COMMENT  'The id of the store for which the product belongs',
    name VARCHAR(50) NOT NULL COMMENT 'The name of the product',
    description TEXT NOT NULL COMMENT 'Description of the product',
    sku VARCHAR(10) NOT NULL COMMENT 'Product SKU code',
    price DECIMAL(20, 2) NOT NULL COMMENT 'Price of the product',
    FOREIGN KEY (store_id) REFERENCES store (store_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;

CREATE TABLE stock (
    stock_id   BIGINT  NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    stock_count BIGINT NOT NULL,
    PRIMARY KEY (stock_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1;