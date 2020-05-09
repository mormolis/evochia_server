CREATE TABLE client_credentials (
    client_id BINARY(16) NOT NULL,
    secret VARCHAR(300) NOT NULL,
    device VARCHAR(50),
    PRIMARY KEY (client_id)
);

CREATE TABLE access_tokens (
    token BINARY(16) NOT NULL,
    token_expiry TIMESTAMP NOT NULL,
    refresh_token BINARY(16) NOT NULL,
    refresh_token_expiry TIMESTAMP NOT NULL,
    user_id BINARY(16) NOT NULL,
    client_id BINARY(16) NOT NULL,
    roles VARCHAR(150),

    PRIMARY KEY (token),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (client_id) REFERENCES client_credentials (client_id),
    INDEX(refresh_token)
);