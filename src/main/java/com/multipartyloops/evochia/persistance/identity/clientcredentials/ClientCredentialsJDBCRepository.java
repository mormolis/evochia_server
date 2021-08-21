package com.multipartyloops.evochia.persistance.identity.clientcredentials;

import com.multipartyloops.evochia.core.identity.dtos.ClientCredentialsDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.multipartyloops.evochia.persistance.identity.clientcredentials.ClientCredentialsSQLStatements.*;

@Repository
public class ClientCredentialsJDBCRepository implements ClientCredentialsRepository<ClientCredentialsDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public ClientCredentialsJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public List<ClientCredentialsDto> getAll() {
        return jdbcTemplate.query(CLIENT_CREDENTIALS_SELECT_ALL,
                this::parseClientCredentials);
    }

    @Override
    public void storeClientCredentials(ClientCredentialsDto clientCredentialsDto) {
        Object clientId = uuidPersistenceTransformer.fromString(clientCredentialsDto.getClientId());
        jdbcTemplate.update(
                CLIENT_CREDENTIALS_INSERTION,
                clientId,
                clientCredentialsDto.getSecret(),
                clientCredentialsDto.getDevice()
        );
    }

    @Override
    public ClientCredentialsDto getByClientId(String clientId) {
        Object binaryClientId = uuidPersistenceTransformer.fromString(clientId);
        List<ClientCredentialsDto> query = jdbcTemplate.query(CLIENT_CREDENTIALS_SELECT_BY_CLIENT_ID, this::parseClientCredentials, binaryClientId);
        if (query.size() == 1) {
            return query.get(0);
        }
        throw new RowNotFoundException("Client not found");
    }

    @Override
    public void deleteByClientId(String clientId) {
        Object binaryClientId = uuidPersistenceTransformer.fromString(clientId);
        jdbcTemplate.update(CLIENT_CREDENTIALS_DELETE_BY_CLIENT_ID, binaryClientId);
    }

    ClientCredentialsDto parseClientCredentials(ResultSet resultSet, int _rowNumber) throws SQLException {
        return new ClientCredentialsDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("client_id")),
                resultSet.getString("secret"),
                resultSet.getString("device")
        );
    }
}
