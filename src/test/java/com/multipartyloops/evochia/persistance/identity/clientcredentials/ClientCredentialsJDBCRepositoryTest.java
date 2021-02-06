package com.multipartyloops.evochia.persistance.identity.clientcredentials;

import com.multipartyloops.evochia.core.identity.entities.ClientCredentialsDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ClientCredentialsJDBCRepositoryTest {

    private static final String CLIENT_CREDENTIALS_INSERTION = "INSERT INTO client_credentials (client_id, secret, device) VALUES (?, ?, ?)";
    private static final String CLIENT_CREDENTIALS_SELECT_BY_CLIENT_ID = "SELECT * FROM client_credentials WHERE client_id=?";
    private static final String CLIENT_CREDENTIALS_SELECT_ALL = "SELECT * FROM client_credentials";
    private static final String CLIENT_CREDENTIALS_DELETE_BY_CLIENT_ID = "DELETE FROM client_credentials WHERE client_id=?";


    @Mock
    private JdbcTemplate jdbcTemplateMock;

    @Mock
    private UuidPersistenceTransformer uuidPersistenceTransformerMock;


    private final String aClientId = UUID.randomUUID().toString();
    private final byte[] aClientIdBytes = aClientId.getBytes(StandardCharsets.UTF_8);

    private final String aSecret = "a-secret";
    private final String aDevice = "a-secret";
    private final ClientCredentialsDto clientCredentialsDto = new ClientCredentialsDto(aClientId, aSecret, aDevice);

    private ClientCredentialsJDBCRepository clientCredentialsJDBCRepository;

    @BeforeEach
    void init() {
        clientCredentialsJDBCRepository = new ClientCredentialsJDBCRepository(jdbcTemplateMock, uuidPersistenceTransformerMock);
    }

    @Test
    void clientsCanBeRetrieved() {
        given(jdbcTemplateMock.query(eq(CLIENT_CREDENTIALS_SELECT_ALL), any(RowMapper.class))).willReturn(List.of(clientCredentialsDto, clientCredentialsDto));

        final List<ClientCredentialsDto> all = clientCredentialsJDBCRepository.getAll();

        assertThat(all).isEqualTo(List.of(clientCredentialsDto, clientCredentialsDto));
    }

    @Test
    void resultSetForClientsCanBeParsed() throws SQLException {
        ResultSet resultSetMock = mock(ResultSet.class);
        given(resultSetMock.getBytes("client_id")).willReturn(aClientIdBytes);
        given(resultSetMock.getString("secret")).willReturn(aSecret);
        given(resultSetMock.getString("device")).willReturn(aDevice);
        given(uuidPersistenceTransformerMock.getUUIDFromBytes(aClientIdBytes)).willReturn(aClientId);

        final ClientCredentialsDto result = clientCredentialsJDBCRepository.parseClientCredentials(resultSetMock, -1);

        assertThat(result).isEqualTo(clientCredentialsDto);
    }

    @Test
    void clientCredentialsCanBeStored() {
        given(uuidPersistenceTransformerMock.fromString(aClientId)).willReturn(aClientIdBytes);

        clientCredentialsJDBCRepository.storeClientCredentials(clientCredentialsDto);

        then(jdbcTemplateMock).should().update(CLIENT_CREDENTIALS_INSERTION, aClientIdBytes, aSecret, aDevice);
    }

    @Test
    void clientCanBeRetrievedById() {
        given(uuidPersistenceTransformerMock.fromString(aClientId)).willReturn(aClientIdBytes);
        given(jdbcTemplateMock.query(eq(CLIENT_CREDENTIALS_SELECT_BY_CLIENT_ID), any(RowMapper.class), (Object) eq(aClientIdBytes))).willReturn(List.of(clientCredentialsDto));

        final ClientCredentialsDto result = clientCredentialsJDBCRepository.getByClientId(aClientId);

        assertThat(result).isEqualTo(clientCredentialsDto);
    }

    @Test
    void retrieveByIdWillThrowWhenClientIdIsNotInDb() {
        given(uuidPersistenceTransformerMock.fromString(aClientId)).willReturn(aClientIdBytes);
        given(jdbcTemplateMock.query(eq(CLIENT_CREDENTIALS_SELECT_BY_CLIENT_ID), any(RowMapper.class), (Object) eq(aClientIdBytes))).willReturn(Collections.emptyList());

        assertThatThrownBy(() -> clientCredentialsJDBCRepository.getByClientId(aClientId))
                .isInstanceOf(RowNotFoundException.class)
                .hasMessage("Client not found");
    }

    @Test
    void clientCanBeDeleted() {
        given(uuidPersistenceTransformerMock.fromString(aClientId)).willReturn(aClientIdBytes);

        clientCredentialsJDBCRepository.deleteByClientId(aClientId);

        then(jdbcTemplateMock).should().update(CLIENT_CREDENTIALS_DELETE_BY_CLIENT_ID, aClientIdBytes);
    }

}