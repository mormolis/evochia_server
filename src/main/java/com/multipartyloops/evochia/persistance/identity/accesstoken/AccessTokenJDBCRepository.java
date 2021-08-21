package com.multipartyloops.evochia.persistance.identity.accesstoken;

import com.multipartyloops.evochia.core.identity.dtos.AccessTokenDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.multipartyloops.evochia.persistance.identity.accesstoken.AccessTokenSQLStatements.*;

@Repository
public class AccessTokenJDBCRepository implements AccessTokensRepository<AccessTokenDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public AccessTokenJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public AccessTokenDto getByAccessToken(String token) {
        Object binaryToken = uuidPersistenceTransformer.fromString(token);
        List<AccessTokenDto> queryResults = jdbcTemplate.query(ACCESS_TOKEN_SELECT_BY_TOKEN, this::parseToken, binaryToken);
        if(queryResults.size() == 1){
            return queryResults.get(0);
        }
        throw new RowNotFoundException("Access Token not found");
    }

    @Override
    public AccessTokenDto getByRefreshToken(String refreshToken) {
        Object binaryRefreshToken = uuidPersistenceTransformer.fromString(refreshToken);
        List<AccessTokenDto> queryResults = jdbcTemplate.query(ACCESS_TOKEN_SELECT_BY_REFRESH_TOKEN, this::parseToken, binaryRefreshToken);
        if(queryResults.size() == 1){
            return queryResults.get(0);
        }
        throw new RowNotFoundException("Refresh Token could not be found");
    }

    @Override
    public List<AccessTokenDto> getAllTokens() {
        return jdbcTemplate.query(ACCESS_TOKEN_SELECT_ALL, this::parseToken);
    }

    @Override
    public void storeAccessToken(AccessTokenDto accessTokenDto) {

        Object binaryAccessToken = uuidPersistenceTransformer.fromString(accessTokenDto.getToken());
        Object binaryRefreshToken = uuidPersistenceTransformer.fromString(accessTokenDto.getRefreshToken());
        Object binaryUserId = uuidPersistenceTransformer.fromString(accessTokenDto.getUserId());
        Object binaryClientId = uuidPersistenceTransformer.fromString(accessTokenDto.getClientId());

        jdbcTemplate.update(ACCESS_TOKEN_INSERTION,
                binaryAccessToken,
                accessTokenDto.getTokenExpiry(),
                binaryRefreshToken,
                accessTokenDto.getRefreshTokenExpiry(),
                binaryUserId,
                binaryClientId,
                accessTokenDto.getRoles()
        );
    }

    @Override
    public void deleteAccessToken(String token) {
        Object binaryToken = uuidPersistenceTransformer.fromString(token);
        jdbcTemplate.update(ACCESS_TOKEN_DELETION, binaryToken);
    }

    private AccessTokenDto parseToken(ResultSet resultSet, int _rowNumber) throws SQLException {
            return new AccessTokenDto(
                    uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("token")),
                    resultSet.getTimestamp("token_expiry").toLocalDateTime(),
                    uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("refresh_token")),
                    resultSet.getTimestamp("refresh_token_expiry").toLocalDateTime(),
                    uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("user_id")),
                    uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("client_id")),
                    resultSet.getString("roles")
            );
    }
}
