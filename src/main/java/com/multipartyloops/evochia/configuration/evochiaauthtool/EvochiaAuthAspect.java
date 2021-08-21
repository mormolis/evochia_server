package com.multipartyloops.evochia.configuration.evochiaauthtool;

import com.multipartyloops.evochia.configuration.exceptions.AccessTokenNotProvidedException;
import com.multipartyloops.evochia.configuration.exceptions.InvalidAccessTokenException;
import com.multipartyloops.evochia.configuration.exceptions.TokenNotInTheRightFormatException;
import com.multipartyloops.evochia.configuration.exceptions.UnauthorizedUserException;
import com.multipartyloops.evochia.core.identity.accesstoken.AccessTokenService;
import com.multipartyloops.evochia.core.identity.commons.RolesConverter;
import com.multipartyloops.evochia.core.identity.dtos.ValidateTokenResponseDto;
import com.multipartyloops.evochia.core.identity.user.dtos.Roles;
import com.multipartyloops.evochia.persistance.exceptions.RowNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Aspect
@Configuration
public class EvochiaAuthAspect {

    private final AccessTokenService accessTokenService;
    private final RolesConverter rolesConverter;

    public EvochiaAuthAspect(AccessTokenService accessTokenService, RolesConverter rolesConverter) {
        this.accessTokenService = accessTokenService;
        this.rolesConverter = rolesConverter;
    }


    @Before("@annotation(authRequirement)")
    public void checkUserValidation(JoinPoint joinPoint, AuthRequirement authRequirement) {

        String token = getAuthenticationTokenFromArguments(joinPoint.getArgs());
        ValidateTokenResponseDto validateTokenResponseDto = validateToken(token);

        checkAccessTokensValidity(validateTokenResponseDto);

        List<Roles> rolesAllowed = Arrays.asList(authRequirement.allow());
        checkIfUserIsAuthorized(rolesAllowed, validateTokenResponseDto);
    }

    private ValidateTokenResponseDto validateToken(String token) {
        try {
            return accessTokenService.validateTokenWithoutClientCredentials(token);
        } catch (RowNotFoundException e) {
            throw new InvalidAccessTokenException("The provided access token is invalid");
        }
    }

    private void checkIfUserIsAuthorized(List<Roles> rolesAllowed, ValidateTokenResponseDto validateTokenResponseDto) {
        if (theRoleIsNotAllowedThisAction(validateTokenResponseDto.getRoles(), rolesAllowed)) {
            throw new UnauthorizedUserException("User must be of the following: " + rolesAllowed);
        }
    }

    private void checkAccessTokensValidity(ValidateTokenResponseDto validateTokenResponseDto) {
        if (!validateTokenResponseDto.isValid()) {
            throw new InvalidAccessTokenException("The provided access token is invalid");
        }
    }

    @SuppressWarnings("unchecked")
    private String getAuthenticationTokenFromArguments(Object[] args) {
        String authorizationHeader = "";
        for (Object arg : args) {
            if (arg instanceof Map) {
                Map<String, String> headers = (Map<String, String>) arg;
                authorizationHeader = headers.keySet().stream()
                        .filter(key -> key.equalsIgnoreCase("authorization"))
                        .findFirst()
                        .map(headers::get)
                        .orElseThrow(() -> new AccessTokenNotProvidedException("Access token has not been provided"));
            }
        }

        return extractTokenFromHeader(authorizationHeader);
    }

    private boolean theRoleIsNotAllowedThisAction(String roles, List<Roles> rolesAllowed) {
        return rolesConverter.fromString(roles)
                .stream()
                .noneMatch(rolesAllowed::contains);
    }

    private String extractTokenFromHeader(String headerValue) {
        if (headerValue.startsWith("Bearer ")) {
            return headerValue.substring("Bearer ".length());
        }
        throw new TokenNotInTheRightFormatException("Access token provided is not in the right format");
    }
}
