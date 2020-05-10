package com.multipartyloops.evochia.core.identity.accesstoken;

import com.multipartyloops.evochia.core.identity.entities.AccessTokenDto;
import com.multipartyloops.evochia.core.identity.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class AccessTokenServiceRefreshTest extends AccessTokenBaseTest {

    @Test
    void accessTokenCanBeIssuedByRefreshTokenAndClientCredentials() {
        AccessTokenDto retrievedByRefresh = new AccessTokenDto(AN_ACCESS_TOKEN, NOW, A_REFRESH_TOKEN, IN_AN_HOUR, A_USER_ID, A_VALID_CLIENT_ID, "ADMIN");
        given(clientCredentialsServiceMock.isPairValid(A_VALID_CLIENT_ID, A_VALID_SECRET))
                .willReturn(true);
        given(accessTokenRepositoryMock.getByRefreshToken(A_REFRESH_TOKEN))
                .willReturn(retrievedByRefresh);

        AccessTokenDto refreshed = accessTokenService.issueAccessTokenByRefreshToken(A_VALID_CLIENT_ID, A_VALID_SECRET, A_REFRESH_TOKEN);

        then(accessTokenRepositoryMock).should().storeAccessToken(refreshed);
        then(accessTokenRepositoryMock).should().deleteAccessToken(retrievedByRefresh.getToken());
        assertThat(refreshed.getClientId()).isEqualTo(retrievedByRefresh.getClientId());
        assertThat(refreshed.getUserId()).isEqualTo(retrievedByRefresh.getUserId());
    }

    @Test
    void refreshTokenRequestShouldProvideValidClientCredentials() {

        given(clientCredentialsServiceMock.isPairValid(AN_INVALID_CLIENT_ID, A_VALID_SECRET))
                .willReturn(false);

        assertThatThrownBy(() -> accessTokenService.issueAccessTokenByRefreshToken(AN_INVALID_CLIENT_ID, A_VALID_SECRET, A_REFRESH_TOKEN))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid ClientId or Secret");
    }

    @Test
    void willThrowExceptionWhenRefreshTokenHasExpired() {

        AccessTokenDto retrievedByRefresh = new AccessTokenDto(AN_ACCESS_TOKEN, NOW, A_REFRESH_TOKEN, AN_HOUR_AGO, A_USER_ID, A_VALID_CLIENT_ID, "ADMIN");
        given(clientCredentialsServiceMock.isPairValid(A_VALID_CLIENT_ID, A_VALID_SECRET))
                .willReturn(true);
        given(accessTokenRepositoryMock.getByRefreshToken(A_REFRESH_TOKEN))
                .willReturn(retrievedByRefresh);

        assertThatThrownBy(() -> accessTokenService.issueAccessTokenByRefreshToken(A_VALID_CLIENT_ID, A_VALID_SECRET, A_REFRESH_TOKEN))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Refresh token has expired");
    }

}