package com.multipartyloops.evochia.persistance.identity.accesstoken;

import com.multipartyloops.evochia.core.identity.entities.AccessTokenDto;
import com.multipartyloops.evochia.core.identity.user.entities.Roles;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AccessTokenJDBCRepositoryTest {

    public static final String A_TOKEN = UUID.randomUUID().toString();
    public static final String A_USER_ID = UUID.randomUUID().toString();
    public static final String A_CLIENT_ID = UUID.randomUUID().toString();
    public static final String A_REFRESH_TOKEN = UUID.randomUUID().toString();
    public static final LocalDateTime expiry = LocalDateTime.now().plusHours(1);
    public static final AccessTokenDto accessTokenDto = new AccessTokenDto(A_TOKEN, expiry, A_REFRESH_TOKEN, expiry, A_USER_ID, A_CLIENT_ID, Roles.STAFF.name());
    static final String ACCESS_TOKEN_SELECT_BY_TOKEN = "SELECT * FROM access_tokens WHERE token=?";
    static final String ACCESS_TOKEN_SELECT_BY_REFRESH_TOKEN = "SELECT * FROM access_tokens WHERE refresh_token=?";
    static final String ACCESS_TOKEN_SELECT_ALL = "SELECT * FROM access_tokens";
    static final String ACCESS_TOKEN_INSERTION = "INSERT INTO access_tokens (token, token_expiry, refresh_token, refresh_token_expiry, user_id, client_id, roles) VALUES (?, ?, ?, ?, ?, ?, ?)";
    static final String ACCESS_TOKEN_DELETION = "DELETE FROM access_tokens WHERE token=?";


    @Mock
    private JdbcTemplate jdbcTemplateMock;

    @Mock
    private UuidPersistenceTransformer uuidPersistenceTransformerMock;


    private AccessTokenJDBCRepository accessTokenJDBCRepository;

    @BeforeEach
    void init() {
        accessTokenJDBCRepository = new AccessTokenJDBCRepository(jdbcTemplateMock, uuidPersistenceTransformerMock);
    }


    @Test
    void retrievesAccessTokenDbEntryByToken() {
        final byte[] binaryToken = A_TOKEN.getBytes();
        given(uuidPersistenceTransformerMock.fromString(A_TOKEN)).willReturn(binaryToken);
        given(jdbcTemplateMock.query(eq(ACCESS_TOKEN_SELECT_BY_TOKEN), any(RowMapper.class), (Object) eq(binaryToken))).willReturn(List.of(accessTokenDto));

        final AccessTokenDto result = accessTokenJDBCRepository.getByAccessToken(A_TOKEN);

        assertThat(result).isEqualTo(accessTokenDto);
    }

    @Test
    void throwsExceptionWhenTokenIsNotInDb() {
        final byte[] binaryToken = A_TOKEN.getBytes();
        given(uuidPersistenceTransformerMock.fromString(A_TOKEN)).willReturn(binaryToken);
        given(jdbcTemplateMock.query(eq(ACCESS_TOKEN_SELECT_BY_TOKEN), any(RowMapper.class), (Object) eq(binaryToken))).willReturn(Collections.emptyList());

        assertThatThrownBy(() -> accessTokenJDBCRepository.getByAccessToken(A_TOKEN))
                .isInstanceOf(RowNotFoundException.class)
                .hasMessage("Access Token not found");
    }

    @Test
    void retrievesAccessTokenDbEntryByRefreshToken() {
        final byte[] binaryToken = A_REFRESH_TOKEN.getBytes();
        given(uuidPersistenceTransformerMock.fromString(A_REFRESH_TOKEN)).willReturn(binaryToken);
        given(jdbcTemplateMock.query(eq(ACCESS_TOKEN_SELECT_BY_REFRESH_TOKEN), any(RowMapper.class), (Object) eq(binaryToken))).willReturn(List.of(accessTokenDto));

        final AccessTokenDto result = accessTokenJDBCRepository.getByRefreshToken(A_REFRESH_TOKEN);

        assertThat(result).isEqualTo(accessTokenDto);
    }

    @Test
    void throwsExceptionWhenRefreshTokenIsNotInDb() {
        final byte[] binaryToken = A_REFRESH_TOKEN.getBytes();
        given(uuidPersistenceTransformerMock.fromString(A_REFRESH_TOKEN)).willReturn(binaryToken);
        given(jdbcTemplateMock.query(eq(ACCESS_TOKEN_SELECT_BY_REFRESH_TOKEN), any(RowMapper.class), (Object) eq(binaryToken))).willReturn(Collections.emptyList());

        assertThatThrownBy(() -> accessTokenJDBCRepository.getByRefreshToken(A_REFRESH_TOKEN))
                .isInstanceOf(RowNotFoundException.class)
                .hasMessage("Refresh Token could not be found");
    }

    @Test
    void retrievesAllAccessTokenDbEntries() {
        given(jdbcTemplateMock.query(eq(ACCESS_TOKEN_SELECT_ALL), any(RowMapper.class))).willReturn(List.of(accessTokenDto, accessTokenDto));

        final List<AccessTokenDto> result = accessTokenJDBCRepository.getAllTokens();

        assertThat(result).isEqualTo(List.of(accessTokenDto, accessTokenDto));
    }

    @Test
    void accessTokenIsStoredInTheDb() {
        final byte[] tokenBytes = accessTokenDto.getToken().getBytes();
        given(uuidPersistenceTransformerMock.fromString(accessTokenDto.getToken())).willReturn(tokenBytes);
        final byte[] refreshTokenBytes = accessTokenDto.getRefreshToken().getBytes();
        given(uuidPersistenceTransformerMock.fromString(accessTokenDto.getRefreshToken())).willReturn(refreshTokenBytes);
        final byte[] userIdBytes = accessTokenDto.getUserId().getBytes();
        given(uuidPersistenceTransformerMock.fromString(accessTokenDto.getUserId())).willReturn(userIdBytes);
        final byte[] clientIdBytes = accessTokenDto.getClientId().getBytes();
        given(uuidPersistenceTransformerMock.fromString(accessTokenDto.getClientId())).willReturn(clientIdBytes);

        accessTokenJDBCRepository.storeAccessToken(accessTokenDto);

        then(jdbcTemplateMock).should()
                .update(ACCESS_TOKEN_INSERTION,
                        tokenBytes,
                        accessTokenDto.getTokenExpiry(),
                        refreshTokenBytes,
                        accessTokenDto.getTokenExpiry(),
                        userIdBytes,
                        clientIdBytes,
                        accessTokenDto.getRoles());
    }

    @Test
    void accessTokensCanBeDeleted() {
        given(uuidPersistenceTransformerMock.fromString(A_TOKEN)).willReturn(A_TOKEN.getBytes());

        accessTokenJDBCRepository.deleteAccessToken(A_TOKEN);

        then(jdbcTemplateMock).should().update(ACCESS_TOKEN_DELETION, A_TOKEN.getBytes());
    }

}