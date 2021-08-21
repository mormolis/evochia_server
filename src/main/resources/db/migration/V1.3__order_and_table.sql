CREATE TABLE table_grouping (
    group_id BINARY(16) NOT NULL,
    group_name VARCHAR(50) UNIQUE,
    enabled BOOLEAN DEFAULT true,
    PRIMARY KEY (group_id),
    INDEX (group_name)
);

CREATE TABLE table_info (
    table_id BINARY(16) NOT NULL,
    table_alias VARCHAR(50) UNIQUE NOT NULL,
    group_id BINARY(16) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    PRIMARY KEY (table_id),
    FOREIGN KEY (group_id) REFERENCES table_grouping (group_id),
    INDEX (table_alias)
);

CREATE TABLE order_info (
    order_id BINARY(16) NOT NULL,
    table_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    order_contents TEXT NOT NULL,
    PRIMARY KEY (order_id),
    FOREIGN KEY (table_id) REFERENCES table_info (table_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);
--
-- CREATE TABLE order_contents (
--     order_contents_id BINARY(16) NOT NULL,
--     order_id BINARY(16) NOT NULL,
--     product_id BINARY(16) NOT NULL,
--     notes VARCHAR(200),
--     paid BOOLEAN NOT NULL,
--     last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     time_put TIMESTAMP NOT NULL,
--     discount_percentage SMALLINT,
--     fixed_discount SMALLINT,
--     PRIMARY KEY (order_contents_id),
--     FOREIGN KEY (order_id) REFERENCES order_info (order_id),
--     FOREIGN KEY (product_id) REFERENCES product (product_id),
--     INDEX (order_id)
-- );
--
-- CREATE TABLE order_content_product_options (
--     order_content_product_options_id BINARY(16) NOT NULL,
--     order_contents_id BINARY(16) NOT NULL,
--     product_option_id BINARY(16) NOT NULL,
--     PRIMARY KEY (order_content_product_options_id),
--     FOREIGN KEY (order_contents_id) REFERENCES order_contents (order_contents_id),
--     FOREIGN KEY (product_option_id) REFERENCES product_options (option_id),
--     INDEX (order_contents_id)
-- );

