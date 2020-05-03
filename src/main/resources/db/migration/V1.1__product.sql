CREATE TABLE product_categories (
    category_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (category_id),
    INDEX (name)
);

CREATE TABLE product (
    product_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(300),
    price_int_part SMALLINT NOT NULL,
    price_fractional_part TINYINT NOT NULL,
    PRIMARY KEY (product_id),
    FOREIGN KEY (category_id) REFERENCES product_categories (category_id),
    INDEX (category_id)
);
-- options depending on the product, example: product: coffee, option: medium, black, milk, sugar etc
CREATE TABLE product_options (
    option_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    variation VARCHAR(50) NOT NULL,
    price_int_part SMALLINT DEFAULT 0,
    price_fractional_part TINYINT DEFAULT 0,
    PRIMARY KEY (option_id),
    FOREIGN KEY (product_id) REFERENCES product (product_id),
    INDEX (product_id)
);