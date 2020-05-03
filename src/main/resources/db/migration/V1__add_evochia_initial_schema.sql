CREATE TABLE users (
    user_id BINARY(16) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(300) NOT NULL,
    name VARCHAR(50),
    telephone VARCHAR(20),
    PRIMARY KEY (user_id),
    INDEX (username)
);

CREATE TABLE roles (
    role_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (role_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    UNIQUE (user_id, role),
    INDEX (user_id)
);

CREATE TABLE table_info (
    table_id BINARY(16) NOT NULL,
    number INT UNIQUE,
    table_alias VARCHAR(50) UNIQUE,
    PRIMARY KEY (table_id),
    INDEX (number)
);

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

-- ORDER TABLES

CREATE TABLE order_info (
    order_id BINARY(16) NOT NULL,
    table_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (order_id),
    FOREIGN KEY (table_id) REFERENCES table_info (table_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE order_contents (
    order_contents_id BINARY(16) NOT NULL,
    order_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    notes VARCHAR(200),
    paid VARCHAR(20) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    time_put TIMESTAMP NOT NULL,
    PRIMARY KEY (order_contents_id),
    FOREIGN KEY (order_id) REFERENCES order_info (order_id),
    FOREIGN KEY (product_id) REFERENCES product (product_id),
    INDEX (order_id)
);

CREATE TABLE order_content_product_options (
    order_content_product_options_id BINARY(16) NOT NULL,
    order_contents_id BINARY(16) NOT NULL,
    product_option_id BINARY(16) NOT NULL,
    PRIMARY KEY (order_content_product_options_id),
    FOREIGN KEY (order_contents_id) REFERENCES order_contents (order_contents_id),
    FOREIGN KEY (product_option_id) REFERENCES product_options (option_id),
    INDEX (order_contents_id)
);