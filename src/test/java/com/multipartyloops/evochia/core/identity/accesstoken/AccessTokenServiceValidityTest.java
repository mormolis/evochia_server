package com.multipartyloops.evochia.core.identity.accesstoken;

import com.multipartyloops.evochia.core.identity.dtos.AccessTokenDto;
import com.multipartyloops.evochia.core.identity.dtos.ValidateTokenResponseDto;
import com.multipartyloops.evochia.core.identity.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class AccessTokenServiceValidityTest extends AccessTokenBaseTest {

    @Test
    void aTokenIsValidWhenExpirationDateIsInTheFuture() {
        AccessTokenDto retrievedFromTheDb = new AccessTokenDto(AN_ACCESS_TOKEN, IN_AN_HOUR, A_REFRESH_TOKEN, IN_AN_HOUR, A_USER_ID, A_VALID_CLIENT_ID, "ADMIN");
        ValidateTokenResponseDto expected = new ValidateTokenResponseDto(true, A_USER_ID, A_VALID_CLIENT_ID, "ADMIN");
        given(clientCredentialsServiceMock.isPairValid(A_VALID_CLIENT_ID,A_VALID_SECRET)).willReturn(true);
        given(accessTokenRepositoryMock.getByAccessToken(AN_ACCESS_TOKEN))
                .willReturn(retrievedFromTheDb);

        ValidateTokenResponseDto validateTokenResponseDto = accessTokenService.validateToken(A_VALID_CLIENT_ID, A_VALID_SECRET, AN_ACCESS_TOKEN);

        assertThat(validateTokenResponseDto).isEqualTo(expected);
    }

    @Test
    void aTokenCannotBeValidatedWhenClientCredentialsAreNotCorrect(){

        given(clientCredentialsServiceMock.isPairValid(AN_INVALID_CLIENT_ID,A_VALID_SECRET)).willReturn(false);

        assertThatThrownBy(()->accessTokenService.validateToken(AN_INVALID_CLIENT_ID, A_VALID_SECRET, A_REFRESH_TOKEN))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid ClientId or Secret");
    }

    @Test
    void aTokenIsInvalidWhenExpirationDateIsInThePast(){
        AccessTokenDto retrievedFromTheDb = new AccessTokenDto(AN_ACCESS_TOKEN, AN_HOUR_AGO, A_REFRESH_TOKEN, IN_AN_HOUR, A_USER_ID, A_VALID_CLIENT_ID, "ADMIN");
        ValidateTokenResponseDto expected = new ValidateTokenResponseDto(false, A_USER_ID, A_VALID_CLIENT_ID, "ADMIN");
        given(clientCredentialsServiceMock.isPairValid(A_VALID_CLIENT_ID,A_VALID_SECRET)).willReturn(true);
        given(accessTokenRepositoryMock.getByAccessToken(AN_ACCESS_TOKEN))
                .willReturn(retrievedFromTheDb);

        ValidateTokenResponseDto validateTokenResponseDto = accessTokenService.validateToken(A_VALID_CLIENT_ID, A_VALID_SECRET, AN_ACCESS_TOKEN);

        assertThat(validateTokenResponseDto).isEqualTo(expected);
    }

}