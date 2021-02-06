package com.multipartyloops.evochia.persistance.identity.clientcredentials;

final class ClientCredentialsSQLStatements {

    static final String CLIENT_CREDENTIALS_INSERTION = "INSERT INTO client_credentials (client_id, secret, device) VALUES (?, ?, ?)";
    static final String CLIENT_CREDENTIALS_SELECT_BY_CLIENT_ID = "SELECT * FROM client_credentials WHERE client_id=?";
    static final String CLIENT_CREDENTIALS_SELECT_ALL = "SELECT * FROM client_credentials";
    static final String CLIENT_CREDENTIALS_DELETE_BY_CLIENT_ID = "DELETE FROM client_credentials WHERE client_id=?";

    private ClientCredentialsSQLStatements() {
    }
}
