package com.multipartyloops.evochia.persistance.identity;

import com.multipartyloops.evochia.core.identity.entities.AccessTokenDto;
import com.multipartyloops.evochia.persistance.JDBCTest;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import com.multipartyloops.evochia.persistance.identity.accesstoken.AccessTokenJDBCRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccessTokenJDBCRepositoryTest extends JDBCTest {

    private AccessTokenJDBCRepository accessTokenJDBCRepository;

    @BeforeEach
    void setup(){
        accessTokenJDBCRepository = new AccessTokenJDBCRepository(new JdbcTemplate(dataSource), new UuidPersistenceTransformer());
    }

    @Test
    void tokenCanBeInsertedAndRetrieved(){
        String accessToken = UUID.randomUUID().toString();
        String clientId = createClientId();
        String userId = createUserId();
        AccessTokenDto accessTokenDto = new AccessTokenDto(accessToken, LocalDateTime.parse("2020-05-09T11:44:26"), UUID.randomUUID().toString(), LocalDateTime.parse("2020-05-09T11:44:26"), userId, clientId, "STAFF");

        accessTokenJDBCRepository.storeAccessToken(accessTokenDto);

        assertThat(accessTokenJDBCRepository.getByAccessToken(accessToken)).isEqualTo(accessTokenDto);
    }

    @Test
    void attemptToReceiveANonExistingTokenThrowsException(){
        String aTokenThatDoesNotExistInTheDb = UUID.randomUUID().toString();

        assertThatThrownBy(()->accessTokenJDBCRepository.getByAccessToken(aTokenThatDoesNotExistInTheDb))
                .isInstanceOf(RowNotFoundException.class)
                .hasMessage("Access Token not found");
    }

    @Test
    void canRetrieveAllTokens(){
        List<AccessTokenDto> tokensToInsert = generateThreeTokens();
        storeListOfAccessTokens(tokensToInsert);

        List<AccessTokenDto> allTokens = accessTokenJDBCRepository.getAllTokens();

        assertThat(allTokens).containsAll(tokensToInsert);
    }

    private void storeListOfAccessTokens(List<AccessTokenDto> tokensToInsert) {
        tokensToInsert
                .forEach(token->accessTokenJDBCRepository.storeAccessToken(token));
    }

    @Test
    void canRetrieveByRefreshToken(){
        List<AccessTokenDto> tokensToInsert = generateThreeTokens();
        storeListOfAccessTokens(tokensToInsert);

        AccessTokenDto retrievedByRefreshToken = accessTokenJDBCRepository.getByRefreshToken(tokensToInsert.get(0).getRefreshToken());

        assertThat(retrievedByRefreshToken).isEqualTo(tokensToInsert.get(0));
    }

    @Test
    void throwsExceptionWhenRetrieveTokenCannotBeFound(){
        String refreshTokenThatDoesNotExistInTheDb = UUID.randomUUID().toString();
        assertThatThrownBy(()->accessTokenJDBCRepository.getByRefreshToken(refreshTokenThatDoesNotExistInTheDb))
                .isInstanceOf(RowNotFoundException.class)
                .hasMessage("Refresh Token could not be found");
    }

    private List<AccessTokenDto> generateThreeTokens() {
        String clientId = createClientId();
        String userId = createUserId();
        return List.of(
                new AccessTokenDto(UUID.randomUUID().toString(), LocalDateTime.parse("2020-05-09T11:44:26"), UUID.randomUUID().toString(), LocalDateTime.parse("2020-05-09T11:44:26"), userId, clientId, "STAFF"),
                new AccessTokenDto(UUID.randomUUID().toString(), LocalDateTime.parse("2020-05-09T11:44:26"), UUID.randomUUID().toString(), LocalDateTime.parse("2020-05-09T11:44:26"), userId, clientId, "STAFF ADMIN"),
                new AccessTokenDto(UUID.randomUUID().toString(), LocalDateTime.parse("2020-05-09T11:44:26"), UUID.randomUUID().toString(), LocalDateTime.parse("2020-05-09T11:44:26"), userId, clientId, "STAFF")
        );
    }


    private String createClientId(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String clientId = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO client_credentials (client_id, secret, device) VALUES (?, ?, ?)",
                convertUuidToBinary(clientId),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
                );
        return clientId;
    }

    private String createUserId(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String userId = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO users (user_id, username, password, name, telephone) VALUES (?, ?, ?, ?, ?)",
                convertUuidToBinary(userId), UUID.randomUUID().toString(), "aPassword", "name", "telephungen"
        );
        return userId;
    }

    private Object convertUuidToBinary(String stringUuid){
        UUID uuid = UUID.fromString(stringUuid);
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return bytes;
    }
}