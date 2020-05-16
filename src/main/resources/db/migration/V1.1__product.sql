CREATE TABLE product_categories (
    category_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT true,
    PRIMARY KEY (category_id),
    INDEX (name)
);

CREATE TABLE product (
    product_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(300),
    price DECIMAL(8,2) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    PRIMARY KEY (product_id),
    FOREIGN KEY (category_id) REFERENCES product_categories (category_id),
    INDEX (category_id),
    INDEX (enabled)
);
-- options depending on the product, example: product: coffee, option: medium, black, milk, sugar etc
CREATE TABLE product_options (
    option_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    variation VARCHAR(50) NOT NULL,
    price DECIMAL(8,2) DEFAULT 0.00,
    PRIMARY KEY (option_id),
    FOREIGN KEY (product_id) REFERENCES product (product_id),
    INDEX (product_id)
);