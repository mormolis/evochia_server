package com.multipartyloops.evochia.configuration.evochiaauthtool;

import com.multipartyloops.evochia.configuration.exceptions.AccessTokenNotProvidedException;
import com.multipartyloops.evochia.configuration.exceptions.InvalidAccessTokenException;
import com.multipartyloops.evochia.configuration.exceptions.TokenNotInTheRightFormatException;
import com.multipartyloops.evochia.configuration.exceptions.UnauthorizedUserException;
import com.multipartyloops.evochia.core.identity.accesstoken.AccessTokenService;
import com.multipartyloops.evochia.core.identity.commons.RolesConverter;
import com.multipartyloops.evochia.core.identity.dtos.ValidateTokenResponseDto;
import com.multipartyloops.evochia.core.identity.user.dtos.Roles;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EvochiaAuthAspectTest {

    public static final String AN_ACCESS_TOKEN = UUID.randomUUID().toString();

    @Mock
    private AccessTokenService accessTokenServiceMock;

    @Mock
    private JoinPoint joinPointMock;

    @Mock
    private AuthRequirement authRequirementMock;

    @Mock
    private RolesConverter rolesConverterMock;

    private EvochiaAuthAspect evochiaAuthAspect;

    @BeforeEach
    void setup() {
        evochiaAuthAspect = new EvochiaAuthAspect(accessTokenServiceMock, rolesConverterMock);
    }

    @Test
    void extractsTheTokenFromHeaders() {

        given(joinPointMock.getArgs()).willReturn(controllersArgumentsWithAuthHeaders());
        given(accessTokenServiceMock.validateTokenWithoutClientCredentials(AN_ACCESS_TOKEN)).willReturn(aToken(true, "ADMIN"));
        given(authRequirementMock.allow()).willReturn(new Roles[]{Roles.ADMIN});
        given(rolesConverterMock.fromString("ADMIN")).willReturn(List.of(Roles.ADMIN));

        evochiaAuthAspect.checkUserValidation(joinPointMock, authRequirementMock);

        then(accessTokenServiceMock).should().validateTokenWithoutClientCredentials(AN_ACCESS_TOKEN);
    }

    @Test
    void throwsAccessTokenNotFoundWhenHeaderIsNotPresent() {
        Map<String, String> headers = new HashMap<>();
        headers.put("a_header", "a value");
        Object[] expected = new Object[]{headers};
        given(joinPointMock.getArgs()).willReturn(expected);

        assertThatThrownBy(() -> evochiaAuthAspect.checkUserValidation(joinPointMock, authRequirementMock))
                .isInstanceOf(AccessTokenNotProvidedException.class)
                .hasMessage("Access token has not been provided");
    }

    @Test
    void throwsInvalidAccessTokenExceptionWhenAccessTokenIsNotValid() {

        given(joinPointMock.getArgs()).willReturn(controllersArgumentsWithAuthHeaders());
        given(accessTokenServiceMock.validateTokenWithoutClientCredentials(AN_ACCESS_TOKEN)).willReturn(aToken(false, "ADMIN"));

        assertThatThrownBy(() -> evochiaAuthAspect.checkUserValidation(joinPointMock, authRequirementMock))
                .isInstanceOf(InvalidAccessTokenException.class)
                .hasMessage("The provided access token is invalid");
    }

    @Test
    void throwsUnauthorisedUserExceptionWhenTheRoleAssociatedWithUsersTokenIsNotSupported() {
        given(joinPointMock.getArgs()).willReturn(controllersArgumentsWithAuthHeaders());
        given(authRequirementMock.allow()).willReturn(new Roles[]{Roles.ADMIN});
        given(rolesConverterMock.fromString("STAFF FINANCE")).willReturn(List.of(Roles.STAFF, Roles.FINANCE));
        given(accessTokenServiceMock.validateTokenWithoutClientCredentials(AN_ACCESS_TOKEN))
                .willReturn(aToken(true, "STAFF FINANCE"));

        assertThatThrownBy(() -> evochiaAuthAspect.checkUserValidation(joinPointMock, authRequirementMock))
                .isInstanceOf(UnauthorizedUserException.class)
                .hasMessage("User must be of the following: [ADMIN]");
    }

    @Test
    void throwTokenNotInTheRightFormatExceptionWhenHeadedDoesNotStartWithBearer(){
        given(joinPointMock.getArgs()).willReturn(controllersArgumentsWithBadAuthHeaders());

        assertThatThrownBy(() -> evochiaAuthAspect.checkUserValidation(joinPointMock, authRequirementMock))
                .isInstanceOf(TokenNotInTheRightFormatException.class)
                .hasMessage("Access token provided is not in the right format");
    }


    //write test about authorization (ROLES CHECK)

    private Object[] controllersArgumentsWithAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("aHeader", "a value");
        headers.put("Authorization", "Bearer " + AN_ACCESS_TOKEN);
        headers.put("anotherHeader", "a value");
        return new Object[]{"a_method_parameter", headers, "another_method"};
    }

    private Object[] controllersArgumentsWithBadAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("aHeader", "a value");
        headers.put("Authorization", "Bourer " + AN_ACCESS_TOKEN);
        headers.put("anotherHeader", "a value");
        return new Object[]{"a_method_parameter", headers, "another_method"};
    }

    private ValidateTokenResponseDto aToken(boolean isTokenValid, String roles) {
        return new ValidateTokenResponseDto(
                isTokenValid,
                "aUserId",
                "aClientId",
                roles
        );
    }


}