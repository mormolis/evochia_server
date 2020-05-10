package com.multipartyloops.evochia.core.identity.user;

import com.multipartyloops.evochia.core.commons.PasswordService;
import com.multipartyloops.evochia.core.identity.user.entities.Roles;
import com.multipartyloops.evochia.core.identity.user.entities.UserAuthenticationDto;
import com.multipartyloops.evochia.core.identity.user.entities.UserDto;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {

    private static final String A_VALID_USERNAME = "aValidUserName";
    private static final String A_VALID_PASSWORD = "aValidPassword";
    private static final String AN_INVALID_PASSWORD = "anInvalidPassword";
    private static final String A_HASHED_PASSWORD = "aHashedPassword";
    public static final String A_USER_ID = UUID.randomUUID().toString();
    public static final String AN_USER_NAME_THAT_DOES_NOT_EXIST = "anUserNameThatDoesNotExist";

    @Mock
    private UserService userServiceMock;

    @Mock
    private PasswordService passwordServiceMock;

    private UserAuthenticationService userAuthenticationService;

    @BeforeEach
    void setup() {
        userAuthenticationService = new UserAuthenticationService(userServiceMock, passwordServiceMock);
    }

    @Test
    void returnsAUserWithObfuscatedPasswordWhenUsernameAndPasswordMatches() {
        UserDto userByUsername = new UserDto(A_USER_ID, A_VALID_USERNAME, A_HASHED_PASSWORD, List.of(Roles.ADMIN, Roles.STAFF), "aName", "aTelephone");
        given(userServiceMock.getUserByUserName(A_VALID_USERNAME)).willReturn(userByUsername);
        given(passwordServiceMock.passwordsAreTheSame(A_VALID_PASSWORD, A_HASHED_PASSWORD)).willReturn(true);

        UserAuthenticationDto userAuthenticationDto = userAuthenticationService.authenticateUser(A_VALID_USERNAME, A_VALID_PASSWORD).get();

        assertThat(userAuthenticationDto)
                .isEqualTo(new UserAuthenticationDto(userByUsername.getUserId(), userByUsername.getRoles()));
    }

    @Test
    void invalidPasswordWillReturnEmptyOptional() {
        UserDto userByUsername = new UserDto(A_USER_ID, A_VALID_USERNAME, A_HASHED_PASSWORD, List.of(Roles.ADMIN, Roles.STAFF), "aName", "aTelephone");
        given(userServiceMock.getUserByUserName(A_VALID_USERNAME)).willReturn(userByUsername);
        given(passwordServiceMock.passwordsAreTheSame(AN_INVALID_PASSWORD, A_HASHED_PASSWORD)).willReturn(false);

        Optional<UserAuthenticationDto> userAuthenticationDto = userAuthenticationService.authenticateUser(A_VALID_USERNAME, AN_INVALID_PASSWORD);

        assertThat(userAuthenticationDto.isEmpty())
                .isTrue();
    }

    @Test
    void willReturnEmptyOptionalWhenUserIsNotFound() {
        given(userServiceMock.getUserByUserName(AN_USER_NAME_THAT_DOES_NOT_EXIST)).willThrow(new RowNotFoundException(""));

        Optional<UserAuthenticationDto> anUserNameThatDoesNotExist = userAuthenticationService.authenticateUser(AN_USER_NAME_THAT_DOES_NOT_EXIST, A_VALID_PASSWORD);

        assertThat(anUserNameThatDoesNotExist.isEmpty()).isTrue();
    }

    @Test
    void deactivatedUserCannotBeAuthenticated() {
        UserDto userByUsername = new UserDto(A_USER_ID, A_VALID_USERNAME, A_HASHED_PASSWORD, List.of(Roles.DEACTIVATED), "aName", "aTelephone");
        given(userServiceMock.getUserByUserName(A_VALID_USERNAME)).willReturn(userByUsername);
        given(passwordServiceMock.passwordsAreTheSame(A_VALID_PASSWORD, A_HASHED_PASSWORD)).willReturn(true);

        Optional<UserAuthenticationDto> authenticationInfo = userAuthenticationService.authenticateUser(A_VALID_USERNAME, A_VALID_PASSWORD);

        assertThat(authenticationInfo.isEmpty())
                .isTrue();
    }

}