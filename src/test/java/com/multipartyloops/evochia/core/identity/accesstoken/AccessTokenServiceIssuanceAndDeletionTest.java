package com.multipartyloops.evochia.core.identity.accesstoken;

import com.multipartyloops.evochia.core.identity.dtos.AccessTokenDto;
import com.multipartyloops.evochia.core.identity.exceptions.InvalidCredentialsException;
import com.multipartyloops.evochia.core.identity.user.dtos.Roles;
import com.multipartyloops.evochia.core.identity.user.dtos.UserAuthenticationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AccessTokenServiceIssuanceAndDeletionTest extends AccessTokenBaseTest {

    @Test
    void issuesAccessTokenWhenAllCredentialsAreValid() {
        List<Roles> listOfRoles = List.of(Roles.ADMIN, Roles.FINANCE);
        String roles = "ADMIN FINANCE";
        UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(A_USER_ID, listOfRoles);
        ArgumentCaptor<AccessTokenDto> captor = ArgumentCaptor.forClass(AccessTokenDto.class);
        given(clientCredentialsServiceMock.isPairValid(A_VALID_CLIENT_ID, A_VALID_SECRET))
                .willReturn(true);
        given(userAuthenticationServiceMock.authenticateUser(A_VALID_USER_NAME, A_VALID_PASSWORD))
                .willReturn(Optional.of(userAuthenticationDto));
        given(rolesConverterMock.fromList(listOfRoles)).willReturn(roles);

        AccessTokenDto accessTokenDto = accessTokenService.issueToken(A_VALID_CLIENT_ID, A_VALID_SECRET, A_VALID_USER_NAME, A_VALID_PASSWORD);

        then(accessTokenRepositoryMock).should().storeAccessToken(captor.capture());
        AccessTokenDto captured = captor.getValue();
        assertThat(captured.getRoles()).isEqualTo(roles);
        assertThat(captured.getUserId()).isEqualTo(A_USER_ID);
        assertThat(captured.getTokenExpiry())
                .isAfter(LocalDateTime.now().plusMinutes(58))
                .isBefore(LocalDateTime.now().plusMinutes(61));
        assertThat(captured.getRefreshTokenExpiry())
                .isAfter(LocalDateTime.now().plusMinutes(119))
                .isBefore(LocalDateTime.now().plusMinutes(121));
        assertThat(accessTokenDto).isEqualTo(captured);
    }

    @Test
    void accessTokenIssuanceShouldThrowInvalidCredentialsExceptionWhenClientOrSecretAreInvalid() {
        given(clientCredentialsServiceMock.isPairValid(AN_INVALID_CLIENT_ID, AN_INVALID_SECRET)).willReturn(false);

        assertThatThrownBy(() -> accessTokenService.issueToken(AN_INVALID_CLIENT_ID, AN_INVALID_SECRET, A_VALID_USER_NAME, A_VALID_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid ClientId or Secret");
    }

    @Test
    void accessTokenIssuanceShouldThrowInvalidCredentialsExceptionWhenUserCredentialsAreInvalid() {
        given(clientCredentialsServiceMock.isPairValid(A_VALID_CLIENT_ID, A_VALID_SECRET)).willReturn(true);
        given(userAuthenticationServiceMock.authenticateUser(A_VALID_USER_NAME, AN_INVALID_PASSWORD)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accessTokenService.issueToken(A_VALID_CLIENT_ID, A_VALID_SECRET, A_VALID_USER_NAME, AN_INVALID_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid Username or Password");

        then(accessTokenRepositoryMock).shouldHaveNoInteractions();
    }

    @Test
    void accessTokenCanBeDeletedGivenValidCredentials() {
        given(clientCredentialsServiceMock.isPairValid(A_VALID_CLIENT_ID, A_VALID_SECRET)).willReturn(true);

        accessTokenService.deleteAccessToken(A_VALID_CLIENT_ID, A_VALID_SECRET, AN_ACCESS_TOKEN);

        then(accessTokenRepositoryMock).should().deleteAccessToken(AN_ACCESS_TOKEN);
    }

    @Test
    void accessTokenCannotBeDeletedWithInvalidClientCredentials(){
        given(clientCredentialsServiceMock.isPairValid(AN_INVALID_CLIENT_ID, AN_INVALID_SECRET)).willReturn(false);

        assertThatThrownBy(() -> accessTokenService.deleteAccessToken(AN_INVALID_CLIENT_ID, AN_INVALID_SECRET, AN_ACCESS_TOKEN))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid ClientId or Secret");
    }
}