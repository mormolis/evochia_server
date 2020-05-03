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