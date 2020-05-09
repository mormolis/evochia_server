package com.multipartyloops.evochia.persistance.identity.accesstoken;

final class AccessTokenSQLStatements {
    static final String ACCESS_TOKEN_SELECT_ALL = "SELECT * FROM access_tokens";
    static final String ACCESS_TOKEN_SELECT_BY_TOKEN = "SELECT * FROM access_tokens WHERE token=?";
    static final String ACCESS_TOKEN_SELECT_BY_REFRESH_TOKEN = "SELECT * FROM access_tokens WHERE refresh_token=?";
    static final String ACCESS_TOKEN_INSERTION = "INSERT INTO access_tokens (token, token_expiry, refresh_token, refresh_token_expiry, user_id, client_id, roles) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private AccessTokenSQLStatements() {
    }
}
