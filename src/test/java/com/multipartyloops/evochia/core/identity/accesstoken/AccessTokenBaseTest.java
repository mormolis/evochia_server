package com.multipartyloops.evochia.core.identity.accesstoken;

import com.multipartyloops.evochia.core.identity.commons.RolesConverter;
import com.multipartyloops.evochia.core.identity.client.ClientCredentialsService;
import com.multipartyloops.evochia.core.identity.entities.AccessTokenDto;
import com.multipartyloops.evochia.core.identity.user.UserAuthenticationService;
import com.multipartyloops.evochia.persistance.identity.accesstoken.AccessTokensRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
abstract class AccessTokenBaseTest {

    public static final String A_VALID_CLIENT_ID = UUID.randomUUID().toString();
    public static final String AN_INVALID_CLIENT_ID = UUID.randomUUID().toString();
    public static final String A_VALID_SECRET = "aValidSecret";
    public static final String A_VALID_USER_NAME = "aValidUserName";
    public static final String A_VALID_PASSWORD = "aValidPassword";
    public static final String A_USER_ID = UUID.randomUUID().toString();
    public static final String AN_INVALID_SECRET = "anInvalidSecret";
    public static final String AN_INVALID_PASSWORD = "anInvalidPassword";
    public static final String A_REFRESH_TOKEN = UUID.randomUUID().toString();
    public static final String AN_ACCESS_TOKEN = UUID.randomUUID().toString();

    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final LocalDateTime AN_HOUR_AGO = NOW.minusHours(1);
    public static final LocalDateTime IN_AN_HOUR = NOW.plusHours(1);


    @Mock
    protected AccessTokensRepository<AccessTokenDto> accessTokenRepositoryMock;
    @Mock
    protected UserAuthenticationService userAuthenticationServiceMock;
    @Mock
    protected ClientCredentialsService clientCredentialsServiceMock;
    @Mock
    protected RolesConverter rolesConverterMock;

    protected AccessTokenService accessTokenService;

    @BeforeEach
    void setup() {
        accessTokenService = new AccessTokenService(
                accessTokenRepositoryMock,
                userAuthenticationServiceMock,
                clientCredentialsServiceMock,
                rolesConverterMock
        );
    }
}
